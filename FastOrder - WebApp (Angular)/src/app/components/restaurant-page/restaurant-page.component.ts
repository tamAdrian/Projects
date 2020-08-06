import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { of, BehaviorSubject, Observable } from 'rxjs';
import { RestaurantPageService } from './restaurant-page.service';
import { RestaurantPage } from 'src/app/models/restaurant-page.model';
import { take } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { UploadImageService } from 'src/app/service/upload-image.service';

@Component({
  selector: 'app-restaurant-page',
  templateUrl: './restaurant-page.component.html',
  styleUrls: ['./restaurant-page.component.css'],
})
export class RestaurantPageComponent implements OnInit {
  frmStepOne: FormGroup;
  filename: string;
  isProfileCompleted = new BehaviorSubject<boolean>(false);
  restaurantNameParam: string;
  showSpinner: boolean = true;
  latitude: number;
  longitude: number;

  constructor(
    private formBuilder: FormBuilder,
    public service: RestaurantPageService,
    public uploadImageService: UploadImageService,
    private snackbar: MatSnackBar,
    private activeRoute: ActivatedRoute
  ) {

    this.setupUploaImageService();
    this.filename = '';

    //default values
    this.frmStepOne = this.formBuilder.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(20),
        ],
      ],
      description: [
        '',
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(40),
        ],
      ],
      imageInput: [this.filename, Validators.required]
    });

    //populate form with existing data
    this.activeRoute.params.subscribe((params) => {
      console.log('Params: ' + params['restaurantName']);
      this.restaurantNameParam = params['restaurantName'];
      this.populateFormControl();
      this.showSpinner = false;
    });

    this.frmStepOne.valueChanges.subscribe((value) => {
      console.log('formStepOne ' + JSON.stringify(value));
    });
  }

  private setupUploaImageService() {
    this.uploadImageService.path = 'restaurant_photos';
    // reinit service values
    this.uploadImageService.uploadProgress = new Observable<number>();
    this.uploadImageService.downloadURL = new Observable<string>();
  }

  ngOnInit(): void {
    this.isProfileCompleted.subscribe((isCompleted) => {
      if (isCompleted) {
        this.snackbar.open(
          'Pagina restaurantului a fost configurata cu succes! Configurati urmatorii pasi.',
          'X',
          {
            duration: 4000,
            panelClass: ['mat-primary', 'mat-toolbar'],
          }
        );
      }
    });
  }

  private populateFormControl() {
    this.service
      .findRestaurant(this.restaurantNameParam)
      .valueChanges()
      .pipe(take(1))
      .subscribe((restaurants) => {
        if (restaurants.length === 1) {
          let restaurant = restaurants[0] as RestaurantPage;
          console.log('ngOnInit: find if user completed restaurant page');
          this.frmStepOne.setValue({
            name: restaurant.name,
            description: restaurant.description,
            imageInput: 'Image was saved',
          });
          this.latitude = restaurant.latitude;
          this.longitude = restaurant.longitude;
          this.uploadImageService.downloadURL = of(restaurant.imageURL);
          this.uploadImageService.imageURL = restaurant.imageURL;
          this.isProfileCompleted.next(true);
          this.frmStepOne.markAsUntouched();
        }
      });
  }

  get name() {
    return this.frmStepOne.get('name');
  }

  get description() {
    return this.frmStepOne.get('description');
  }

  chooseFile(event) {
    this.uploadImageService.upload(event);
    this.frmStepOne.get('imageInput').setValue('Image was chose');
  }

  saveRestaurantPage() {
    const restaurantPage = new RestaurantPage(
      this.uploadImageService.imageURL,
      this.latitude,
      this.longitude,
      this.name.value,
      this.description.value
    );

    this.service.saveRestaurantPage(restaurantPage);
    console.log('RestaurantPage ' + restaurantPage.name + ' was saved');
    this.isProfileCompleted.next(true);
    this.frmStepOne.markAsUntouched();
  }
}
