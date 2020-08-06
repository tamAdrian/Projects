import { Injectable } from '@angular/core';
import { take } from 'rxjs/operators';
import { AngularFireDatabase } from 'angularfire2/database';
import { RestaurantPage } from 'src/app/models/restaurant-page.model';

@Injectable({
  providedIn: 'root',
})
export class RestaurantPageService {
  private basePath: string = '/restaurants';
  ref: any;

  constructor(
    private db: AngularFireDatabase
  ) {
    this.ref = db.list(this.basePath);
  }

  
  findRestaurant(restaurantName: string) {
    //check if restaurant exists
    return this.db
      .list(this.basePath, (ref) =>
        ref.orderByChild('name').equalTo(restaurantName)
      );
  }

  saveRestaurantPage(restaurantPage: RestaurantPage) {
    this.findRestaurant(restaurantPage.name)
      .valueChanges()
      .pipe(take(1))
      .subscribe(restaurants => {
        console.log("RestaurantPageService : Save " + restaurantPage.name + " page.")

        let key: String;
        if (restaurants.length == 1) {
          const oldRestaurant = restaurants[0] as RestaurantPage;
          key = oldRestaurant.key;
        } else {
          key = this.ref.push().key
        }
        
        // update or insert new RestaurantPage
        this.ref.update(key, {
          key: key,
          imageURL: restaurantPage.imageURL,
          latitude: Number(restaurantPage.latitude),
          longitude: Number(restaurantPage.longitude),
          name: restaurantPage.name,
          description: restaurantPage.description,
        });
      });
  }

}