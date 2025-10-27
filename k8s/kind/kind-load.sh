docker pull mongo:7.0.5
docker pull mysql:8.3.0
docker pull confluentinc/cp-zookeeper:7.5.0
docker pull confluentinc/cp-kafka:7.5.0
docker pull confluentinc/cp-schema-registry:7.5.0
docker pull provectuslabs/kafka-ui:latest
docker pull mysql:8
docker pull quay.io/keycloak/keycloak:24.0.1
docker pull grafana/loki:main
docker pull prom/prometheus:v2.46.0
docker pull grafana/tempo:2.2.2
docker pull grafana/grafana:10.1.0
docker pull nivakaran/hotelmanagement-api-gateway:latest
docker pull nivakaran/hotelmanagement-booking-service:latest
docker pull nivakaran/hotelmanagement-order-service:latest
docker pull nivakaran/hotelmanagement-guest-service:latest
docker pull nivakaran/hotelmanagement-notification-service:latest
docker pull nivakaran/hotelmanagement-hotel-service:latest
docker pull nivakaran/hotelmanagement-restaurant-service:latest
docker pull nivakaran/hotelmanagement-staff-service:latest
docker pull nivakaran/hotelmanagement-payment-service:latest

kind load docker-image -n hotelmanagement mongo:7.0.5
kind load docker-image -n hotelmanagement mysql:8.3.0
kind load docker-image -n hotelmanagement confluentinc/cp-zookeeper:7.5.0
kind load docker-image -n hotelmanagement confluentinc/cp-kafka:7.5.0
kind load docker-image -n hotelmanagement confluentinc/cp-schema-registry:7.5.0
kind load docker-image -n hotelmanagement provectuslabs/kafka-ui:latest
kind load docker-image -n hotelmanagement mysql:8
kind load docker-image -n hotelmanagement quay.io/keycloak/keycloak:24.0.1
kind load docker-image -n hotelmanagement grafana/loki:main
kind load docker-image -n hotelmanagement prom/prometheus:v2.46.0
kind load docker-image -n hotelmanagement grafana/tempo:2.2.2
kind load docker-image -n hotelmanagement grafana/grafana:10.1.0

kind load docker-image -n hotelmanagement nivakaran/hotelmanagement-api-gateway:latest
kind load docker-image -n hotelmanagement nivakaran/hotelmanagement-booking-service:latest
kind load docker-image -n hotelmanagement nivakaran/hotelmanagement-order-service:latest
kind load docker-image -n hotelmanagement nivakaran/hotelmanagement-guest-service:latest
kind load docker-image -n hotelmanagement nivakaran/hotelmanagement-notification-service:latest
kind load docker-image -n hotelmanagement nivakaran/hotelmanagement-hotel-service:latest
kind load docker-image -n hotelmanagement nivakaran/hotelmanagement-restaurant-service:latest
kind load docker-image -n hotelmanagement nivakaran/hotelmanagement-staff-service:latest
kind load docker-image -n hotelmanagement nivakaran/hotelmanagement-payment-service:latest