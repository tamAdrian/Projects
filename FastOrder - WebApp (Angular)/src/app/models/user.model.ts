export class User {
    activated: boolean;
    email: string;
    restaurant: string;

    constructor(activated: boolean, email: string, restaurant: string) {
        this.activated = activated;
        this.email = email;
        this.restaurant = restaurant;
    }
}
