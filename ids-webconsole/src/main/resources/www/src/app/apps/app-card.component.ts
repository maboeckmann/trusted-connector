import { Component, Input, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

import { App } from './app';
import { AppService } from './app.service';

@Component({
  selector: 'app-card',
  template: `
      <div class="mdl-card__title mdl-card--expand">
        <h2 class="mdl-card__title-text">{{app.names}}</h2>
      </div>
      <div class="mdl-card__supporting-text">
        <div class="mdl-grid">
          <div class="mdl-cell mdl-cell--4-col bold">
            Trust
          </div>
          <div class="mdl-cell mdl-cell--4-col bold">
            Uptime
          </div>
          <div class="mdl-cell mdl-cell--4-col bold">
            Ports
          </div>
          <div class="mdl-cell mdl-cell--4-col">
            <div style="color: #209e91">Trusted</div>
          </div>
          <div class="mdl-cell mdl-cell--4-col">
            {{app.uptime}}
          </div>
          <div class="mdl-cell mdl-cell--4-col">
            {{app.ports}}
          </div>
        </div>
        Created: {{app.created}}<br />
        Status: {{app.status}}<br />
      </div>
      <div class="mdl-card__actions mdl-card--border">
      <button class="mdl-button mdl-js-button mdl-button--fab mdl-button--mini-fab">
        <i class="material-icons">{{statusIcon}}</i>
      </button>

      <button class="mdl-button mdl-js-button mdl-button--fab mdl-button--mini-fab">
        <i class="material-icons">delete</i>
      </button>

        <!--  <a class="mdl-button mdl-js-button mdl-js-ripple-effect"><i class="material-icons" role="presentation">start</i></a>
          <a class="mdl-button mdl-js-button mdl-js-ripple-effect"><i class="material-icons" role="presentation">pause</i></a>
          <a class="mdl-button mdl-js-button mdl-js-ripple-effect"><i class="material-icons" role="presentation">delete</i></a>-->
      </div>`
})
export class AppCardComponent implements OnInit {
  @Input() app: App;
  statusIcon: string;

  constructor( private routeService: AppService) {}
  ngOnInit(): void {
    if(this.app.status.indexOf("Up") >= 0) {
      this.statusIcon = "stop";
    } else {
      this.statusIcon = "play_arrow";

    }
  }

  onToggle(containerId: string): void {
    if(this.statusIcon == "play_arrow") {
      this.statusIcon = "stop";
      this.routeService.startApp(containerId).subscribe(result => {
      });
      this.app.status = 'Up 1 seconds ago';

    } else {
      this.statusIcon = "play_arrow";
      this.routeService.stopApp(containerId).subscribe(result => {
      });
       this.app.status = 'Exited(0) 1 seconds ago';
    }
  }
}
