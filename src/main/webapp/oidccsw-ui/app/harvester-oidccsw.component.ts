
import { Component } from '@angular/core';

@Component({
  selector: 'harvester-oidccsw',
  templateUrl: './harvester-oidccsw.component.html'
})
export class HarvesterOidcCswComponent {
  config = {
    clientId: '',
    clientSecret: '',
    tokenUrl: '',
    cswEndpoint: ''
  };
}
