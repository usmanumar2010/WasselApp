package solutions.webdealer.project.wassel.activities.order;

/**
 * Created by Usman Umar on 16/08/2017.
 */

public class RestaurantsListClass extends RestaurantsList {
    String restaurants;
    String id;

    public String getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(String restaurants) {
        this.restaurants = restaurants;
    }

    public RestaurantsListClass(String restaurants, String id) {
        this.restaurants = restaurants;
        this.id = id;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
