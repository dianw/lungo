[![codecov](https://codecov.io/gh/dianw/lungo/branch/main/graph/badge.svg?token=1zmZYwAysF)](https://codecov.io/gh/dianw/lungo)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=dianw_lungo&metric=coverage)](https://sonarcloud.io/summary/new_code?id=dianw_lungo)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dianw_lungo&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=dianw_lungo)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=dianw_lungo&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=dianw_lungo)


## Running
To run this project you have to install maven on your machine and type the following command inside the project directory:
```bash
$ docker-compose up # optional, run local oidc, smtp and mariadb server locally
$ mvn spring-boot:run # run the actual app
```

Open http://localhost:8080/api-docs/ui on your browser
