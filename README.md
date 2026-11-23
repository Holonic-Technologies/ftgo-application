# Testing guide: governed-behavior review

Open FTGO pull requests against `master`. The Holonic check posts its findings with analysis links in the pull request conversation. The approved
[FTGO specifications](https://49s1mkl7jg.execute-api.us-east-1.amazonaws.com/reports/ftgo/specifications)
are available in the Holonic Web Portal.

This guide helps you, or an automated tester working for you, try the review on your own changes and judge how well it
supports a reviewer's decision.

## What the review does

This pilot uses FTGO’s existing baseline of approved workflows and rules. Automatic workflow discovery from your
source code and recording the rules your team accepts are unavailable in this pilot.

When a pull request arrives, the review compares the behavior of every changed method against that baseline and
shows the reviewer the few differences that matter. Each difference is tied to the workflow step it reaches and
the accepted rule it puts at risk, and its intent is judged against the PR description.

## Why it works

- **Refactors stay quiet.** Methods are compared by what they do on every path: the writes they make, the messages
  they send, the errors they raise and the order of their calls. Renames, extracted helpers, reordered branches and
  loops rewritten as streams all clear.
- **Real changes surface at any size.** A three-line behavior change inside a thousand-line refactor still shows up,
  as one specific item.
- **Every finding is specific.** Each item names the method that changed, what it did before and after, the workflow
  step it reaches and the accepted rule at risk.
- **Your PR description counts.** A change the description explains reads as intended. A change it doesn't mention
  is surfaced for a closer look.
- **Consistent answers.** The same change gets the same result every time it runs.
- **Advisory.** The review informs the reviewer and never blocks a merge.

## Designing test changes

The most useful test set mixes the change types below. Pairing a behavior change with a refactor of the same code is
the clearest way to see the review at work.

| Change you make | What a good run shows |
|---|---|
| A refactor: rename, extract a method, reorder independent branches, turn a loop into a stream | Cleared, with nothing to review |
| A behavior change explained in the PR description | One item, labelled intended, naming the rule |
| The same behavior change with a vague or empty description | One item, labelled uncertain |
| A behavior change described as "no behavior change" | One item, labelled unexpected |
| A change in one service that affects a rule carried out by unchanged code elsewhere | An item on that rule, even though its code didn't change |
| A changed configuration value used by several workflows | One item listing every affected workflow |
| A very large pull request | Routed to manual review, with a note saying why |

Good behavior changes to try in a workflow application:

- pass a different identifier to a command or event
- send a message in a different order, or drop one
- raise a different error, or handle one differently
- change which state a record moves to
- skip or add a compensation step

## Writing PR descriptions

Write descriptions the way you would for a colleague: say what behavior changes and why. The review reads them the
same way. To see the labels at work, run one change several times with different descriptions: accurate, vague, empty
and misleading.

## Scope

Designed for Java services built on Spring, including message-driven workflows and sagas. The review governs the
workflows and rules accepted in the current baseline; workflows added later are covered once the baseline is updated.
