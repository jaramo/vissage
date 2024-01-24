# Messaging Application
[![CircleCI](https://dl.circleci.com/status-badge/img/circleci/WzuQdvtjPKoxEmHy9QgEXM/Juy1xCVrqjr5qjZ5vUcMhE/tree/main.svg?style=svg&circle-token=d302ab8d3a51488f8e339fdb6104539e9346ad0e)](https://dl.circleci.com/status-badge/redirect/circleci/WzuQdvtjPKoxEmHy9QgEXM/Juy1xCVrqjr5qjZ5vUcMhE/tree/main)

## How to run

There is some infrastructure required to run the application locally:
- PostgreSQL
- RabbitMQ

The `docker-compose.yml` file has the configuration required to run the app, 
and the credentials match those in `application.yml` configuration file.

To start the infrastructure execute the following command in the console
```
docker-compose up -d -f docker-compose.yml
```

Running the application could be done in IntelliJ just clicking the play button
or in the command line with the following command
```
./gradlew bootRun
```

### Database bootstrap
Liquibase is used to initialize the DB. It is automatically executed during the start-up process.

The initial schema can be found in `resources/db/changelog/changes/001-init-schema.sql` 
and liquibase configuration in `resources/db/changelog/db.changelog-master.yaml`

### Rabbit bootstrap
An `exchage` with the name `events` is required to have the full functionality.
If the exchange is not present the application won't fail but won't be able to publish any message.

I chose the `Routing` configuration with a direct exchange so multiple queues could be bind to the same routing key and receive a copy of the message.

Documentation [here](https://www.rabbitmq.com/tutorials/tutorial-four-python.html).

## Architectural decisions
To separate code responsibilities a `Ports and Adapters` architecture was used and tested with `ArchUnit` [framework](https://www.archunit.org/userguide/html/000_Index.html#_onion_architecture).

### Spring Data JDBC
As opposed to Spring Data JPA, JDBC entities does not resolve automagically back references and joins between tables.

That's the reason why 1+N queries are required to recover related entities.
Also, it could be implemented as a custom native SQL query with joins and implementing a custom row mapper.

Documentation [example](https://spring.io/blog/2018/09/24/spring-data-jdbc-references-and-aggregates/).

## Future Improvements

### Event Listeners to update messsage status
Since `Messaging Application` is responsible for the `message` domain, it should be responsible to keep the message status up to date.

`message` table has the following columns to track messages evolution 
```
id           uuid      primary key,
sent_at      timestamp not null,
delivered_at timestamp,
read_at      timestamp,
```

Different `RabbitListener` could be implemented to update message information based on the events emitted in the platform, i.e:
- `Queue(name='messaging_messaged_delivered', exchange='events', routing_key='messaging.message.delivered')` 
to execute final action in the DB `update message set delivered_at = ${event.delivered_at} where id = ${event.id}`


- `Queue(name='messaging_messaged_read', exchange='events', routing_key='messaging.message.read')`
  to execute final action in the DB `update message set read_at = ${event.read_at} where id = ${event.id}`
 
## Tests
Tests where split into two different types: `tests` and `integration tests`.
They can be executed separated or together
```
./gradlew test 
./gradlew integrationTest
./gradlew check # all of them together
```

### Integration Tests
Tests that require actual infrastructure to run the test, like `PostgreSQL` db instance or an actual `RabbitMQ` instance,
and they were use TestContainers to spin up the required infra.

Those tests are slower to run since they need to bootstrap all the required infra before executing,
and could lead to issues due to different environment configurations

### Not integration tests
I don't call them unit because here are also `SpringContext` tests, but they don't require any infrastructure to run.
All the external adapters are either mocked in unit tests, or created a test double that replaces the adapter in spring context.

