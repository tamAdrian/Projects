import { MenuItem } from './menu-item.model';

export class Order {
  active: boolean;
  authUser: string;
  id: string;
  orders: MenuItem[];
  restaurantName: string;
  totalMenuItems: number;
  totalPrice: number;

  constructor(
    active: boolean,
    authUser: string,
    id: string,
    orders: MenuItem[],
    restaurantName: string,
    totalMenuItems: number,
    totalPrice: number
  ) {
      this.active = active;
      this.authUser = authUser;
      this.id = id;
      this.orders = orders;
      this.restaurantName = restaurantName;
      this.totalMenuItems = totalMenuItems;
      this.totalPrice = totalPrice;
  }
}
