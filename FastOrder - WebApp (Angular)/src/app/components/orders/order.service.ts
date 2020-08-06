import { Injectable } from '@angular/core';
import { AngularFireDatabase } from 'angularfire2/database';
import { PlacedOrder } from 'src/app/models/placed-order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private basePath: string = '/placed-orders';

  constructor(private db: AngularFireDatabase) { }

  getRestaurantOrders(restaurantName: string) {
    return this.db
    .list(this.basePath, (ref) =>
      ref.orderByChild('restaurantName').equalTo(restaurantName)
    );
  }

  updatePlacedOrder(placedOrder: PlacedOrder) {
    var newStatus =  "";

    if(placedOrder.status === "nepreluata") {
      newStatus = "preluata"
    } else {
      newStatus = "nepreluata"
    }
    
    this.db.list(this.basePath).update(placedOrder.id, {status: newStatus})
    console.log("OrderService: updated placedOrder status");
  }

}
