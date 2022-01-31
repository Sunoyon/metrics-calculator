### Project

This project stores machine parameters and calculates different metrics of them (average, median, min, max) within a time limit.

The project has been developed using spring cloud function. Data is stored in mongodb. All deployment has been managed by docker-compose.

### Dependency

1.  java 11
2.  maven
3.  docker


### Build the project with unit and integration tests

	mvn clean package

	
### Run the project by docker-compose

	docker-compose up --build
	
### Endpoints

Endpoint to insert machine parameters into the datastore

	curl -i -X POST -H "Content-Type: application/json" http://localhost/machines -d '{  "machineKey": "embosser", "parameters": {"core_diameter": 3, "speed": 20 }}'
	
	# Insert parameters mentioned in the `parameters.csv` file
	curl -i -X POST -H "Content-Type: application/json" http://localhost/machines -d '{  "machineKey": "ajoparametrit", "parameters": {"TS_setpoint_tail_length": 15, "perforation_length": 16.5, "core_interference": 15, "number_of_sheets": 17.7 }}'
	curl -i -X POST -H "Content-Type: application/json" http://localhost/machines -d '{  "machineKey": "aufwickler", "parameters": {"log_diameter": 15, "speed": 35.6 }}'
	curl -i -X POST -H "Content-Type: application/json" http://localhost/machines -d '{  "machineKey": "wickelkopf", "parameters": {"core_interference": 25.7, "speed": 27.5 }}'
	
Endpoint to get latest parameters

	curl -i -X GET -H "Content-Type: application/json" http://localhost/machine-latest-parameters
	
Endpoint to get metrics of a machine

	 curl -i -X POST -H "Content-Type: application/json" http://localhost/machine-metrics -d '{  "machineKey": "embosser", "minutesFrom": 100 }'


	
### Miscellaneous

*   Docker script can be found in `Dockerfile`
*   Docker compose environments can be found in `.env`

