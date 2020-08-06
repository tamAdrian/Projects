import { Component, OnInit } from '@angular/core';
import { OrderService } from './order.service';
import { ActivatedRoute } from '@angular/router';
import { PlacedOrder } from 'src/app/models/placed-order.model';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit {
  placedOrders: PlacedOrder[];
  restaurantNameParam: string;
  date: Date;
  showSpinner: boolean = true;

  constructor(
    private service: OrderService,
    private activeRoute: ActivatedRoute
  ) {
    this.date = new Date();
  }

  ngOnInit(): void {
    this.activeRoute.params.subscribe((params) => {
      console.log('Params: ' + params['restaurantName']);
      this.restaurantNameParam = params['restaurantName'];
    });

    this.showSpinner = true;
  
    this.service
      .getRestaurantOrders(this.restaurantNameParam)
      .valueChanges()
      .subscribe((placedOrders: PlacedOrder[]) => {
        console.log("Filter orders based on current day [" + this.date.getDate() + "]");

        this.placedOrders = [];
        placedOrders.map( (placedOrder: PlacedOrder) => {
          var placedOrderDate = placedOrder.date;
          var orderDate = new Date(placedOrderDate.split(",")[0]);
          
          if(orderDate.getDate() === this.date.getDate() && orderDate.getMonth() === this.date.getMonth()) {
            this.placedOrders.push(placedOrder);
          }
        })

        //sort orders by hour
        this.placedOrders.sort((a,b) => a.hour.localeCompare(b.hour))
        this.showSpinner = false;
      });
  }

  changePlacedOrderStatus(placedOrder: PlacedOrder) {
    this.service.updatePlacedOrder(placedOrder);
  }

}
