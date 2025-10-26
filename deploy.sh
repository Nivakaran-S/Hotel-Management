#!/bin/bash

# Deployment script for Hotel Management System
set -e

# Configuration
ENVIRONMENT=${1:-local}
REGISTRY="your-registry.azurecr.io"
NAMESPACE="hotel-management"

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}Deploying Hotel Management System - Environment: $ENVIRONMENT${NC}"

case $ENVIRONMENT in
    "local")
        deploy_local
        ;;
    "docker")
        deploy_docker
        ;;
    "k8s")
        deploy_kubernetes
        ;;
    *)
        echo -e "${RED}Invalid environment. Use: local, docker, or k8s${NC}"
        exit 1
        ;;
esac

deploy_local() {
    echo -e "${YELLOW}Building and running locally with Maven...${NC}"

    # Start infrastructure services
    echo "Starting infrastructure services..."
    docker-compose up -d zookeeper kafka redis mongodb mysql

    # Wait for services to be ready
    echo "Waiting for services to be ready..."
    sleep 30

    # Build and run each service
    SERVICES=("api-gateway" "hotel-service" "guest-service" "booking-service" "restaurant-service" "order-service" "payment-service" "staff-service" "notification-service")

    for service in "${SERVICES[@]}"; do
        echo -e "${BLUE}Starting $service...${NC}"
        cd $service
        ./mvnw spring-boot:run &
        cd ..
        sleep 10
    done

    echo -e "${GREEN}All services started locally!${NC}"
}

deploy_docker() {
    echo -e "${YELLOW}Deploying with Docker Compose...${NC}"

    # Login to registry if needed
    # docker login $REGISTRY

    # Pull latest images
    echo "Pulling latest images..."
    docker-compose -f docker-compose.yml -f docker-compose.override.yml pull

    # Start all services
    echo "Starting all services..."
    docker-compose -f docker-compose.yml -f docker-compose.override.yml up -d

    # Wait for services to be healthy
    echo "Waiting for services to be healthy..."
    sleep 60

    # Check service status
    echo -e "${BLUE}Service Status:${NC}"
    docker-compose -f docker-compose.yml -f docker-compose.override.yml ps

    echo -e "${GREEN}All services deployed with Docker!${NC}"
    echo -e "${BLUE}Access points:${NC}"
    echo "API Gateway: http://localhost:8080"
    echo "MongoDB: localhost:27017"
    echo "MySQL: localhost:3306"
    echo "Redis: localhost:6379"
    echo "Kafka: localhost:9092"
}

deploy_kubernetes() {
    echo -e "${YELLOW}Deploying to Kubernetes...${NC}"

    # Check if kubectl is available
    if ! command -v kubectl &> /dev/null; then
        echo -e "${RED}kubectl is not installed or not in PATH${NC}"
        exit 1
    fi

    # Create namespace if it doesn't exist
    echo "Creating namespace..."
    kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

    # Deploy infrastructure components
    echo "Deploying infrastructure components..."
    kubectl apply -f k8s/infrastructure/ -n $NAMESPACE

    # Wait for infrastructure to be ready
    echo "Waiting for infrastructure to be ready..."
    kubectl wait --for=condition=ready pod -l app=mongodb -n $NAMESPACE --timeout=300s
    kubectl wait --for=condition=ready pod -l app=mysql -n $NAMESPACE --timeout=300s
    kubectl wait --for=condition=ready pod -l app=redis -n $NAMESPACE --timeout=300s
    kubectl wait --for=condition=ready pod -l app=kafka -n $NAMESPACE --timeout=300s

    # Deploy application services
    echo "Deploying application services..."
    kubectl apply -f k8s/manifests/applications/ -n $NAMESPACE

    # Wait for applications to be ready
    echo "Waiting for applications to be ready..."
    sleep 60

    # Check deployment status
    echo -e "${BLUE}Deployment Status:${NC}"
    kubectl get pods -n $NAMESPACE
    kubectl get services -n $NAMESPACE

    # Get ingress information
    echo -e "${BLUE}Ingress Information:${NC}"
    kubectl get ingress -n $NAMESPACE

    echo -e "${GREEN}Kubernetes deployment completed!${NC}"
}

# Health check function
health_check() {
    echo -e "${BLUE}Performing health checks...${NC}"

    if [ "$ENVIRONMENT" = "docker" ]; then
        # Check Docker services
        services=("api-gateway:8080" "hotel-service:8081" "guest-service:8082")
        for service in "${services[@]}"; do
            name=${service%%:*}
            port=${service##*:}
            echo "Checking $name..."
            if curl -f http://localhost:$port/actuator/health > /dev/null 2>&1; then
                echo -e "${GREEN}✓ $name is healthy${NC}"
            else
                echo -e "${RED}✗ $name is not responding${NC}"
            fi
        done
    fi
}

# Cleanup function
cleanup() {
    case $ENVIRONMENT in
        "docker")
            echo -e "${YELLOW}Cleaning up Docker resources...${NC}"
            docker-compose -f docker-compose.yml -f docker-compose.override.yml down -v
            ;;
        "k8s")
            echo -e "${YELLOW}Cleaning up Kubernetes resources...${NC}"
            kubectl delete namespace $NAMESPACE
            ;;
    esac
}

# Trap cleanup on script exit
trap cleanup EXIT

# Run health checks after deployment
if [ "$ENVIRONMENT" = "docker" ]; then
    health_check
fi

echo -e "${GREEN}Deployment completed successfully!${NC}"
