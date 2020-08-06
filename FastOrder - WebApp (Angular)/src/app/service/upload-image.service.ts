import { Injectable } from '@angular/core';
import { AngularFireStorage, AngularFireUploadTask } from 'angularfire2/storage';
import { Observable, Subject } from 'rxjs';
import { finalize } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UploadImageService {

  uploadProgress: Observable<number>;
  downloadURL: Observable<string>;
  imageURL: string;
  task: AngularFireUploadTask;
  uploadSrc: string;
  path: string;

  constructor(private afStorage: AngularFireStorage) {}

  upload(event) : Observable<string> {
      const file = event.target.files[0];
      const path = `${this.path}/${new Date().getTime()}_${file.name}`; //generate unique path
  
      this.task = this.afStorage.upload(path, file);
  
      const ref = this.afStorage.ref(path);
  
      this.uploadProgress = this.task.percentageChanges();
      console.log('uploadImageService: Image uploaded!');
  
      var responseURL = new Subject<string>();
      this.task
        .snapshotChanges()
        .pipe(
          finalize(() => {
            this.downloadURL = ref.getDownloadURL();
            this.downloadURL.subscribe(url => {
                this.imageURL = url;
                responseURL.next(url);
            });
          }))
        .subscribe();
      
      return responseURL.asObservable();
  }
  
}
