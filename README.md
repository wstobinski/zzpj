<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, commits-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![commits][commits-shield]][commits-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/wstobinski/zzpj">
    <img src="readmeUtil/logo.png" alt="Logo" width="280" height="280">
  </a>

  <h3 align="center">HANDBY Handball League Manager</h3>

  <p align="center">
    Your go-to system for managing your handball league!
    <br />
    <a href="https://github.com/othneildrew/Best-README-Template"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/othneildrew/Best-README-Template">View Demo</a>
    ·
    <a href="https://github.com/wstobinski/zzpj/issues">Report Bug</a>
    ·
    <a href="https://github.com/wstobinski/zzpj/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

We believe that sports are for everyone—not just the chosen few! Our project is designed to support local sports league owners and participants in conveniently managing their environments. Say goodbye to Excel spreadsheets and paper sheets—everything is digitalized and accessible right from your phone! Haven't I convinced you yet why you should use our product?

Here's why:

* Your time should be focused on creating something amazing. Minimize paperwork, maximize passion.
* You shouldn't be doing the same tasks over and over again.
* You should always try to make your life easier 😄
  
Of course, no one system will suit all projects since your needs may differ. We'll be adding more features in the near future. You may also suggest changes by forking this repo and creating a pull request or opening an issue. Thanks to everyone who has contributed to expanding this project!

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

[![Angular][Angular.io]][Angular-url]
[![PostgreSQL][PostgreSQL]][PostgreSQL-url]
[![Java][Java]][Java-url]
[![GoogleCloud][GoogleCloud]][GoogleCloud-url]
[![Spring][Spring]][Spring-url]
[![GHActions][GHActions]][GHActions-url]
[![Ionic][Ionic]][Ionic-url]


<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

Here are listed steps developers need to take to launch our app localy.

### Prerequisites

List of properties shown below is required to launch this app as a developer.

Update your npm
  ```sh
  npm install -g npm
  ```
Install Ionic
  ```sh
  npm install -g @ionic/cli
  ```
If you don't have Java installed localy, you will need it! [Here is how](https://www.java.com/en/download/help/windows_manual_download.html)

### Installation

_Below is an example of how you can instruct your audience on installing and setting up your app. This template doesn't rely on any external dependencies or services._

1. Clone the repo
   
   ```sh
   https://github.com/wstobinski/zzpj.git
   ```
2. Install NPM packages
   ```sh
   npm install
   ```
3. Fill in your API and data sources in `application.properties`
   ```js
   spring.datasource.url="your/url"
   spring.datasource.password="YourPassword"
   jwt.secret="yourSecretKey"
   mail.password="yourMailPassword"
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

This system is designed to manage various aspects of a sports league effectively and securely. Users are assigned different roles (Admin, Captain, Referee), each with specific permissions that dictate their available actions within the system.

- **Admins** have full access to all functionalities. They can create new leagues and teams, generate detailed reports, edit team and player information, manage players, change match schedules, generate announcements on the forum, update match statistics, and evaluate referee performance.
- **Captains** can manage and edit their respective teams and players. They have permissions to update their team data, manage team members, and provide feedback on referees' performance. Captains do not have access to league creation, report generation, or schedule changes.
- **Referees** have restricted access primarily focused on match-related functionalities. They can modify match schedules, generate forum posts (such as updates on match timings), and update match statistics. Referees do not have permissions to create leagues, teams, or generate reports.

This comprehensive set of features ensures that each user can efficiently perform their role within the system, facilitating smooth and organized management of the sports league. The system's security measures protect against unauthorized access, and its scalability allows for adjustments based on the league's needs. Additionally, the system is designed to be user-friendly and compliant with relevant regulations, including data protection laws.

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [x] League Management
- [x] Schedule Generation
- [ ] Add sentiment analisys for comments about referees
- [ ] Image scanning photos of match raports 
- [ ] Multi-language Support
    - [ ] Polish
    - [ ] English

See the [open issues](https://github.com/wstobinski/zzpj/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Wojciech Stobiński - [@linkedin](https://www.linkedin.com/in/wojtek-stobiński-3124b5217) - 242538@edu.p.lodz.pl\
Artur Włodarczyk - [@linkedin](https://www.linkedin.com/in/artur-w%C5%82odarczyk/) - 242564@edu.p.lodz.pl

Project Link: [HandBy](https://github.com/wstobinski/zzpj)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/badge/Contributors-6-blue?style=for-the-badge
[contributors-url]: https://github.com/wstobinski/zzpj/graphs/contributors

[commits-shield]: https://img.shields.io/github/commit-activity/t/wstobinski/zzpj?style=for-the-badge&color=%231FB141
[commits-url]: https://github.com/wstobinski/zzpj/commits/main/

[stars-shield]: https://img.shields.io/github/stars/othneildrew/Best-README-Template.svg?style=for-the-badge
[stars-url]: https://github.com/othneildrew/Best-README-Template/stargazers

[issues-shield]: https://img.shields.io/github/issues/wstobinski/zzpj?style=for-the-badge&logo=GitBook&label=Issues
[issues-url]: https://github.com/wstobinski/zzpj/issues

[license-shield]: https://img.shields.io/github/license/wstobinski/zzpj?style=for-the-badge&color=%23C71D23
[license-url]: https://github.com/wstobinski/zzpj/blob/main/LICENSE

[product-screenshot]: images/screenshot.png

[Spring]: https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white
[Spring-url]: https://spring.io/projects/spring-boot/
[PostgreSQL]: https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org.pl/
[GoogleCloud]: https://img.shields.io/badge/GoogleCloud-%234285F4.svg?style=for-the-badge&logo=google-cloud&logoColor=white
[GoogleCloud-url]: https://cloud.google.com/
[Angular.io]: https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[Angular-url]: https://angular.io/
[GHActions]: https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white
[GHActions-url]: https://docs.github.com/en/actions
[Java]: https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.java.com/pl/
[Ionic]: https://img.shields.io/badge/Ionic-%233880FF.svg?style=for-the-badge&logo=Ionic&logoColor=white
[Ionic-url]: https://ionicframework.com/docs
