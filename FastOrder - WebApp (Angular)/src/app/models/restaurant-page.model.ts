export class RestaurantPage {
  key: string = "";
  imageURL: string;
  latitude: number;
  longitude: number;
  name: string;
  description: string;

  constructor(
    imageURL: string,
    latitude: number,
    longitude: number,
    name: string,
    description: string
  ) {
    this.imageURL = imageURL;
    this.latitude = latitude;
    this.longitude = longitude;
    this.name = name;
    this.description = description;
  }

  public toString = (): string => {
    return this.imageURL + " " + this.latitude + " " + this.longitude + " " + this.name + " " + this.description
  };
}
