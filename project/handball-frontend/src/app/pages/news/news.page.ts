import {Component, OnInit} from '@angular/core';
import {GenericPage} from "../generic/generic.page";
import {LoadingService} from "../../services/loading.service";
import {ModalController, PopoverController} from "@ionic/angular";
import {Post} from "../../model/post.model";
import {PostService} from "../../services/post.service";
import {Utils} from "../../utils/utils";
import {Subscription} from "rxjs";
import {User} from "../../model/user.model";
import {UserService} from "../../services/user.service";
import {EditPlayerModalComponent} from "../../components/edit-player-modal/edit-player-modal.component";
import {PostModalComponent} from "../../components/post-modal/post-modal.component";

@Component({
  selector: 'app-news',
  templateUrl: './news.page.html',
  styleUrls: ['./news.page.scss'],
})
export class NewsPage extends GenericPage implements OnInit {

  constructor(private postService: PostService,
              private utils: Utils,
              private userService: UserService,
              private modalController: ModalController,
              loadingService: LoadingService,
              popoverController: PopoverController) {
    super(loadingService, popoverController);
  }

  posts: Post[]
  user: User;
  userSub: Subscription;

  override async ngOnInit() {

    this.userSub = this.userService.getUser().subscribe(u => {
      this.user = u;
    });
    try {
      this.posts = (await this.postService.getAllPosts()).response;
    } catch (e) {
      if (e.status === 401) {
        this.utils.presentAlertToast("Wystąpił problem podczas pobierania ogłoszeń");
      }
    }

  }

  isAdmin() {
    return this.user?.role === 'admin';
  }

  async openAddNewsModal() {
    const modal = await this.modalController.create({
      component: PostModalComponent,
      componentProps: {
        title: "Dodaj nowe ogłoszenie",
        mode: "ADD"
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.data && data.role === 'ADD') {
        this.postService.addNewPost(data.data).then(async r => {
          if (r.ok) {
            this.posts.unshift(r.response)
            this.utils.presentInfoToast("Ogłoszenie opublikowano pomyślnie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas publikowania ogłoszenia");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas publikowania ogłoszenia. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas publikowania ogłoszenia");
          }
        });
      }
    });
    return await modal.present();
  }

  async openEditNewsModal(post: Post) {
    const modal = await this.modalController.create({
      component: PostModalComponent,
      componentProps: {
        title: "Edytuj ogłoszenie",
        mode: "EDIT",
        post
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.data && data.role === 'EDIT') {
        this.postService.editPost(data.data).then(async r => {
          if (r.ok) {
            this.utils.presentInfoToast("Ogłoszenie edytowano pomyślnie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas edytowania ogłoszenia");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas edytowania ogłoszenia. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas edytowania ogłoszenia");
          }
        });
      }
    });
    return await modal.present();
  }

  deletePost(postToDelete: Post) {

    this.utils.presentYesNoActionSheet("Czy na pewno chcesz usunąć to ogłoszenie?", "Tak", "Nie", async () => {
      try {
        const response = await this.postService.deletePost(postToDelete.uuid);
        if (response.ok) {
          this.utils.presentInfoToast("Ogłoszenie zostało usunięte")
          this.posts = this.posts.filter(post => post.uuid !== postToDelete.uuid);
        } else {
          this.utils.presentAlertToast("Wystąpił błąd podczas usuwania ogłoszenia");
        }
      } catch (e) {
        if (e.status === 401) {
          this.utils.presentAlertToast("Wystąpił błąd podczas usuwania ogłoszenia. Twoja sesja wygasła, zaloguj się ponownie");
        } else {
          this.utils.presentAlertToast("Wystąpił błąd podczas usuwania ogłoszenia");
        }
      }

    }, () => {})

  }
}
