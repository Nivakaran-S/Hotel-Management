#!/bin/bash

# Configuration for Docker Hub (Free)
DOCKER_USERNAME="yourdockerhubusername"  # Replace with your Docker Hub username
REGISTRY="docker.io/$DOCKER_USERNAME"
PROJECT_NAME="hotel-management"
VERSION="latest"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}Building and pushing Hotel Management System to Docker Hub...${NC}"
echo -e "${BLUE}Registry: $REGISTRY${NC}"
echo -e "${BLUE}Project: $PROJECT_NAME${NC}"
echo -e "${BLUE}Version: $VERSION${NC}"

# Docker Hub login
echo -e "${BLUE}Logging into Docker Hub...${NC}"
echo "Please enter your Docker Hub credentials:"
docker login

if [ $? -ne 0 ]; then
    echo -e "${RED}Docker login failed. Please check your credentials.${NC}"
    exit 1
fi

# List of services
SERVICES=(
    "api-gateway"
    "hotel-service"
    "guest-service"
    "booking-service"
    "restaurant-service"
    "order-service"
    "payment-service"
    "staff-service"
    "notification-service"
)

# Function to build and push a service
build_and_push_service() {
    local service=$1
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}Building $service...${NC}"
    echo -e "${BLUE}========================================${NC}"
    
    if [ ! -d "$service" ]; then
        echo -e "${RED}Directory $service not found${NC}"
        return 1
    fi
    
    cd $service
    
    # Check if Dockerfile exists
    if [ ! -f "Dockerfile" ]; then
        echo -e "${YELLOW}No Dockerfile found for $service, creating one...${NC}"
        create_dockerfile $service
    fi
    
    # Build with Maven
    if [ -f "pom.xml" ]; then
        echo -e "${BLUE}Building with Maven...${NC}"
        if [ -f "./mvnw" ]; then
            ./mvnw clean package -DskipTests
        else
            mvn clean package -DskipTests
        fi
        
        if [ $? -ne 0 ]; then
            echo -e "${RED}Maven build failed for $service${NC}"
            cd ..
            return 1
        fi
    else
        echo -e "${RED}No pom.xml found for $service${NC}"
        cd ..
        return 1
    fi
    
    # Build Docker image
    echo -e "${BLUE}Building Docker image for $service...${NC}"
    docker build -t $REGISTRY/$PROJECT_NAME-$service:$VERSION .
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Docker build failed for $service${NC}"
        cd ..
        return 1
    fi
    
    # Push to Docker Hub
    echo -e "${BLUE}Pushing $service to Docker Hub...${NC}"
    docker push $REGISTRY/$PROJECT_NAME-$service:$VERSION
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Docker push failed for $service${NC}"
        cd ..
        return 1
    fi
    
    echo -e "${GREEN}✅ Successfully built and pushed $service${NC}"
    cd ..
}

# Function to create Dockerfile if missing
create_dockerfile() {
    local service=$1
    cat > Dockerfile << EOF
FROM openjdk:17-jdk-slim

VOLUME /tmp

# Add application jar
COPY target/*.jar app.jar

# Add wait-for-it script for service dependencies
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "/app.jar"]
EOF
    echo -e "${GREEN}Created Dockerfile for $service${NC}"
}

# Build and push all services
echo -e "${YELLOW}Starting build process for all services...${NC}"
failed_services=()

for service in "${SERVICES[@]}"; do
    build_and_push_service $service
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Failed to build $service${NC}"
        failed_services+=($service)
    else
        echo -e "${GREEN}✅ Successfully completed $service${NC}"
    fi
    echo ""
done

# Summary
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}BUILD SUMMARY${NC}"
echo -e "${BLUE}========================================${NC}"

if [ ${#failed_services[@]} -eq 0 ]; then
    echo -e "${GREEN}🎉 All services built and pushed successfully!${NC}"
    echo -e "${BLUE}Images available at: https://hub.docker.com/u/$DOCKER_USERNAME${NC}"
    echo -e "${BLUE}Registry: $REGISTRY${NC}"
    echo ""
    echo -e "${YELLOW}Next steps:${NC}"
    echo "1. Update docker-compose.override.yml with your Docker Hub username"
    echo "2. Run: ./deploy.sh docker"
    echo "3. Access your application at: http://localhost:8080"
else
    echo -e "${RED}❌ Failed services: ${failed_services[*]}${NC}"
    echo "Please check the errors above and retry."
    exit 1
fi

echo -e "${GREEN}🚀 Ready for deployment!${NC}"
