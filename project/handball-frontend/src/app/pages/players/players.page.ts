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


  async openPlayerDetailsModal(player: Player, mode: 'EDIT' | 'ADD' = 'EDIT') {
    console.log("Entering playerDetails", player)
    const modal = await this.modalController.create({
      component: EditPlayerModalComponent,
      componentProps: {
        player,
        title: mode == "EDIT" ? "Edytuj zawodnika" : "Dodaj nowego zawodnika",
        mode
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.data && data.role === 'EDIT') {
        this.playersService.updatePlayer(data.data as Player).then(r => {
          if (r.ok) {
            this.utils.presentInfoToast("Edycja zawodnika zakończona sukcesem");
            const responsePlayer = r.response as Player;

            const index = this.players.findIndex(player => player.uuid === responsePlayer.uuid);

            if (index !== -1) {
              this.players[index] = responsePlayer;
            }
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
      } else if (data && data.data && data.role === 'ADD') {
        this.playersService.addPlayer(data.data).then(async r => {
          if (r.ok) {
            const newPlayers = await this.playersService.getAllPlayers();
            this.players = newPlayers.response;
            this.utils.presentInfoToast("Utworzenie zawodnika zakończone sukcesem");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia zawodnika");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia zawodnika. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia zawodnika");
          }
        });
      }
    });
    return await modal.present();
  }

  deletePlayer(player: Player) {

    this.utils.presentYesNoActionSheet(`Czy na pewno chcesz usunąć zawodnika ${player.firstName} ${player.lastName}? Ta akcja jest nieodwracalna`, 'Tak, usuwam zawodnika', 'Nie',
      () => {
        this.playersService.deletePlayer(player.uuid).then(async r => {
          if (r.ok) {
            this.players = (await this.playersService.getAllPlayers()).response;
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
