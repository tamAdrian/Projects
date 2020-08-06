export class MenuItem {
    key: string = "";
    category: string;
    foodImageURL: string;
    name: string;
    price: number;
    restaurant: string;
    quantity: number = 0;

    constructor(
        category: string,
        foodImageURL: string,
        name: string,
        price: number,
        restaurant: string
    ) {
        this.category = category;
        this.foodImageURL = foodImageURL;
        this.name = name;
        this.price = price;
        this.restaurant = restaurant;
    }
}
