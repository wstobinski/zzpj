import {Component, OnInit} from '@angular/core';
import {GenericPage} from "../generic/generic.page";
import {LoadingService} from "../../services/loading.service";
import {PlayersService} from "../../services/players.service";
import {ActionButton} from "../../model/action-button.model";
import {Utils} from "../../utils/utils";
import {Player} from "../../model/player.model";
import {ModalController, PopoverController} from "@ionic/angular";
import {EditPlayerModalComponent} from "../../components/edit-player-modal/edit-player-modal.component";

@Component({
  selector: 'app-players',
  templateUrl: './players.page.html',
  styleUrls: ['./players.page.scss'],
})
export class PlayersPage extends GenericPage implements OnInit {

  constructor(private playersService: PlayersService,
              private utils: Utils,
              private modalController: ModalController,
              loadingService: LoadingService,
              popoverController: PopoverController) {
    super(loadingService, popoverController);
  }

  players: Player[];
  actionButtons: ActionButton[];

  override async ngOnInit() {
    super.ngOnInit();
    const playersResponse = await this.playersService.getAllPlayers()
    console.log(playersResponse)
    this.players = playersResponse.response;
    console.log(this.players)
    this.actionButtons = [
      {
        buttonName: "Edytuj zawodnika",
        buttonAction: this.openPlayerDetailsModal.bind(this)
      },
      {
        buttonName: "Usuń zawodnika",
        buttonAction: this.deletePlayer.bind(this),
        actionColor: 'danger'
      },
    ]

  }


  async openPlayerDetailsModal(player: Player) {
    console.log("Entering playerDetails", player)
    const modal = await this.modalController.create({
      component: EditPlayerModalComponent,
      componentProps: {
        player,
        title: `Edytuj zawodnika`
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.role === 'submit') {
        this.playersService.updatePlayer(data.data as Player).then(r => {
          if (r.ok) {

            this.utils.presentInfoToast("Edycja zawodnika zakończona sukcesem");
            player = r.response;
          } else {
            console.log(r);
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji zawodnika");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji zawodnika. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji zawodnika");
          }
        });
      }
    });
    return await modal.present();
  }

  deletePlayer(player: Player) {

    this.utils.presentYesNoActionSheet(`Czy na pewno chcesz usunąć zawodnika ${player.firstName} ${player.lastName}? Ta akcja jest nieodwracalna`, 'Tak, usuwam zawodnika', 'Nie',
      () => {
        this.playersService.deletePlayer(player.uuid).then(r => {
          if (r.ok) {
            this.utils.presentInfoToast(`Zawodnik został usunięty pomyślnie`);
          } else {
            this.utils.presentAlertToast(`Wystąpił błąd przy usuwaniu zawodnika`);
          }
        }).catch(error => {
          this.utils.presentAlertToast("Wystąpił błąd przy usuwaniu zawodnika");
        });
      }, () => {

      });

  }
}
