import { Order } from './order.model';

export class PlacedOrder {
  date: string;
  fcmToken: string;
  hour: string;
  id: string;
  numberOfPeople: number;
  order: Order;
  restaurantName: string;
  status: string;

  constructor(
    date: string,
    fcmToken: string,
    hour: string,
    id: string,
    numberOfPeople: number,
    order: Order,
    restaurantName: string,
    status: string
  ) {
    this.date = date;
    this.fcmToken = fcmToken;
    this.hour = hour;
    this.id = id;
    this.numberOfPeople = numberOfPeople;
    this.order = order;
    this.restaurantName = restaurantName;
    this.status = status;
  }
}
