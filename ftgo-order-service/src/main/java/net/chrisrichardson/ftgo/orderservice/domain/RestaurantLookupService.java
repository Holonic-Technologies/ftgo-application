package net.chrisrichardson.ftgo.orderservice.domain;

import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

public class RestaurantLookupService {

  private final RestaurantRepository restaurantRepository;

  public RestaurantLookupService(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  @Cacheable("restaurantsById")
  public Optional<Restaurant> findRestaurant(long restaurantId) {
    return restaurantRepository.findById(restaurantId);
  }
}
