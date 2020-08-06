import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormArray, AbstractControl } from '@angular/forms';
import { RestaurantMenuService } from './restaurant-menu.service';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { UploadImageService } from 'src/app/service/upload-image.service';
import { MenuItem } from 'src/app/models/menu-item.model';
import { take } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-restaurant-menu',
  templateUrl: './restaurant-menu.component.html',
  styleUrls: ['./restaurant-menu.component.css'],
  providers: [UploadImageService],
})
export class RestaurantMenuComponent implements OnInit {
  frmStepTwo: FormGroup;
  restaurantMenu: Observable<any[]>;
  restaurantNameParam: string;
  editMenuItem = new FormArray([]);
  showSpinner: boolean = true;
  categories: any[];

  constructor(
    private formBuilder: FormBuilder,
    private service: RestaurantMenuService,
    public uploadImageService: UploadImageService,
    private activeRoute: ActivatedRoute,
    private snackbar: MatSnackBar
  ) {
    this.uploadImageService.path = "restaurant_menus"
  }

  ngOnInit(): void {

    this.activeRoute.params.subscribe(params => {
      console.log("Params: " + params['restaurantName']);
      this.restaurantNameParam = params['restaurantName'];
    });

    this.frmStepTwo = this.formBuilder.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(25),
        ],
      ],
      category: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(40),
        ],
      ],
      price: [null, [Validators.required, Validators.pattern('[0-9]*\[.]?[0-9]+$')]],
      imageInput: ["", Validators.required],
      foodImageURL: ""
    });

    this.service.getRestaurantMenu(this.restaurantNameParam).valueChanges()
      .subscribe(menus => {
        this.editMenuItem = new FormArray([]);
        menus.map(menu => {
          let menuItem = menu as MenuItem;
          console.log("Primesc noi meniuri " + menuItem.name)
          
          const group = this.formBuilder.group({
            name: [
              menuItem.name,
              [
                Validators.required,
                Validators.minLength(2),
                Validators.maxLength(25),
              ],
            ],
            category: [
              menuItem.category,
              [
                Validators.required,
                Validators.minLength(2),
                Validators.maxLength(40),
              ],
            ],
            price: [menuItem.price, [Validators.required, Validators.pattern('[0-9]*\[.]?[0-9]+$')]],
            imageInput: ["Image was choose", Validators.required],
            foodImageURL: menuItem.foodImageURL,
            key: menuItem.key
          });
          this.editMenuItem.insert(0, group)
        })
        this.showSpinner = false;
      });

    this.frmStepTwo.valueChanges.subscribe(value => {
      console.log("formStepTwo " + JSON.stringify(value))
    });

    this.categories = [
      { name: 'Supe' },
      { name: 'Burgeri' },
      { name: 'Pizza' },
      { name: 'Salate'}, 
      { name: 'Paste'},
      { name: 'Bauturi'}
  ];
  }


  get name() {
    return this.frmStepTwo.get('name');
  }

  get category() {
    return this.frmStepTwo.get('category');
  }

  get price() {
    return this.frmStepTwo.get('price');
  }

  get foodImageURL() {
    return this.frmStepTwo.get('foodImageURL');
  }

  chooseFile(event) {
    this.uploadImageService.upload(event).pipe(take(1)).subscribe(url => {
      this.foodImageURL.setValue(url);
    })
    this.frmStepTwo.get('imageInput').setValue('Image was chose');
  }

  saveMenuItem() {
    let menuItem = new MenuItem(
      this.category.value,
      this.uploadImageService.imageURL,
      this.name.value,
      Number(this.price.value),
      this.restaurantNameParam
    );

    this.service.saveMenuItem(menuItem);
    this.foodImageURL.setValue('');
    this.resetForm();

    this.snackbar.open(
      'Adaugare efectuata cu succes.',
      'X',
      {
        duration: 2500,
        panelClass: ['mat-primary', 'mat-toolbar'],
      }
    );
  }

  resetForm() {
    this.frmStepTwo.reset();
    this.uploadImageService.uploadProgress = new Observable<number>();
    this.uploadImageService.downloadURL = new Observable<string>();
  }

  chooseFileForMenu(event, selectedMenuItem: AbstractControl) {
    this.uploadImageService.upload(event).pipe(take(1)).subscribe(url => {
      selectedMenuItem.get('foodImageURL').setValue(url);
    })
  }

  editSelectedMenuItem(selectedMenuItem: AbstractControl) {
    var menuItem = new MenuItem(
      selectedMenuItem.get('category').value,
      selectedMenuItem.get('foodImageURL').value,
      selectedMenuItem.get('name').value,
      Number(selectedMenuItem.get('price').value),
      this.restaurantNameParam
    )

    menuItem.key = selectedMenuItem.get('key').value;

    this.service.saveMenuItem(menuItem);
    console.log("RestaurantMenu: " + "edit menu item [" + selectedMenuItem.get('name').value + "]");

    this.snackbar.open(
      'Editare efectuata cu succes.',
      'X',
      {
        duration: 2500,
        panelClass: ['mat-primary', 'mat-toolbar'],
      }
    );
  }

  removeMenuItem(selectedMenuItem: AbstractControl, position: number) {
    this.service.removeMenuItem(selectedMenuItem.get('key').value, selectedMenuItem.get('foodImageURL').value);
    this.editMenuItem.removeAt(position);
  }

  compareObjects(o1: string, o2: string): boolean {
    return o1 === o2;
  }
}