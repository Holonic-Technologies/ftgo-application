/* The single intent-diff presentation model and template, independent of verdict. */
(function(root){
'use strict';
const SCHEMA='intent-finding-v1';
const GENERATION_ERROR='Intent data interpretation encountered a data generation error';
const escape=value=>String(value??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
function model(item){
 const standard=item.standard||item.testQuality||item.findings?.[0]?.kind==='standard';
 if(standard){
  const row=item.presentation||{},rule=item.findings?.[0]||{};
  if(row.status==='error')return {schema:SCHEMA,status:'error',headline:GENERATION_ERROR};
  return {schema:SCHEMA,status:'available',headline:row.headline||item.title,
   contextLabel:item.testQuality?'Test requirement:':'Coding standard:',
   codeIntent:rule.name||row.code_intent||item.before||'',changeLabel:row.status==='partial'?'Analysis status:':item.status==='watch'?'Advisory concern:':'What was flagged:',
   change:row.change||item.detailSummary||item.summary||'',before:row.before||'',after:row.after||'',otherChanges:[]};
 }
 const recorded=item.presentation?.schema===SCHEMA?item.presentation:null;
 const failed=recorded?!['available','partial'].includes(recorded.status):['fallback','unavailable','error'].includes(item.wordingSource);
 // Legacy semantic prose can be read through this adapter; raw fact projections cannot.
 const available=!failed&&Boolean(recorded||item.semanticProse)&&[recorded?.headline||item.title,recorded?.change||item.detailSummary||item.summary].every(value=>typeof value==='string'&&value.trim());
 return {schema:SCHEMA,status:available?'available':'error',
  headline:available?(recorded?.headline||item.title):GENERATION_ERROR,
  codeIntent:recorded?recorded.code_intent||'':item.semanticProse?(item.requirementSummary||item.objective||''):'',
  changeLabel:recorded?.status==='partial'?'Analysis status:':'What PR Achieves:',
  change:available?(recorded?.change||item.detailSummary||item.summary||''):'',
  before:available?(recorded?recorded.before||'':item.before||''):'',after:available?(recorded?recorded.after||'':item.after||''):'',
  otherChanges:recorded?.other_changes||[],references:recorded?.references||{}};
}
// Escape all source text first; only paired inline literal markers gain styling.
const prose=value=>escape(value).replace(/`([^`\n]+)`/g,'<code class="intent-term">$1</code>');
function governedObjectives(packet,obligationIds){
 const ids=new Set(obligationIds),governed=packet.governed_objectives||{};
 const workflows=packet.explorer?.workflows?.workflows||governed.customer_workflows?.workflows||[];
 const objectiveIds=new Set(workflows.flatMap(workflow=>(workflow.obligations||[]).filter(row=>ids.has(row.id)).flatMap(row=>row.governed_objective_ids||[])));
 const records=(governed.holon_map?.mappings||[]).filter(row=>objectiveIds.has(row.id)&&row.statement);
 for(const row of packet.objective_summary?.intent_semantics?.explanations||[])
  if((row.presentation?.references?.obligation_ids||[]).some(id=>ids.has(id)))records.push(...(row.presentation.governed_objectives||[]));
 return [...new Map(records.map(row=>[row.id,{id:row.id,description:row.statement}])).values()];
}
function markup(item,{source='',impacts=''}={}){
 const view=model(item);
 if(view.status==='error')return `<section class="error-box" role="alert"><p>${GENERATION_ERROR}</p></section>${source}`;
 const text=prose;
 return `<div class="tile-head"><h3>${escape(view.headline)}</h3></div>
  ${view.codeIntent?`<section class="intent-section" data-intent-section="code-intent"><p><b>${view.contextLabel||'Code Intent:'}</b> ${text(view.codeIntent)}</p></section>`:''}
  <section class="intent-section" data-intent-section="change"><p><b>${view.changeLabel||'What PR Achieves:'}</b> ${text(view.change)}</p></section>
  ${view.otherChanges.length?`<section class="intent-section" data-intent-section="other-changes"><b>Unintended Changes:</b><ul>${view.otherChanges.map(row=>`<li>${text(row)}</li>`).join('')}</ul></section>`:''}
  ${view.before&&view.after?`<section class="ba" data-intent-section="comparison"><div><small>Before</small>${text(view.before)}</div><div class="after"><small>After</small>${text(view.after)}</div></section>`:''}
  ${impacts?`<section class="finding-impact"><h4>Possible impact of this change based on analysis of code intent</h4>${impacts}</section>`:''}
  <div data-intent-section="source">${source}</div>`;
}
root.codeIntentFinding={SCHEMA,GENERATION_ERROR,model,markup,prose,governedObjectives};
if(typeof module!=='undefined')module.exports=root.codeIntentFinding;
})(typeof window!=='undefined'?window:globalThis);
