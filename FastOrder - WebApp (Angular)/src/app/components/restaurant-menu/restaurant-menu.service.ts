import { Injectable } from '@angular/core';
import { AngularFireDatabase } from 'angularfire2/database';
import { AngularFireStorage } from 'angularfire2/storage';
import { MenuItem } from 'src/app/models/menu-item.model';

@Injectable({
  providedIn: 'root'
})
export class RestaurantMenuService {
  private basePath: string = '/menus';
  ref: any;

  constructor(private afStorage: AngularFireStorage, private db:AngularFireDatabase) {
    this.ref = db.list(this.basePath);
  }

  getRestaurantMenu(restaurantName: string) {
    return this.db
      .list(this.basePath, (ref) =>
        ref.orderByChild('restaurant').equalTo(restaurantName)
      );
  }

  saveMenuItem(menuItem: MenuItem) {
        let key: String;
        
        if(menuItem.key != "") {
          key = menuItem.key;
        }
        else {
          key = this.ref.push().key
        }
      
        // update or insert new MenuItem
        this.ref.update(key, {
          key: key,
          category: menuItem.category,
          name: menuItem.name,
          price: menuItem.price,
          foodImageURL: menuItem.foodImageURL,
          restaurant: menuItem.restaurant
        });

        console.log("RestaurantMenuService: Menu item saved!")
  }

  removeMenuItem(key: string, imageDownloadURL: string) {
    this.ref.remove(key);
    this.afStorage.storage.refFromURL(`${imageDownloadURL}`).delete();
    console.log("RestaurantMenuService: menuItem deleted.")
  }
}