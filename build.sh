# 1. Install Docker Buildx
docker buildx version

# 2. Create a builder that uses Docker's cloud builders
docker buildx create --use --name cloud-builder --driver cloud

# 3. Login to Docker Hub
docker login

# 4. Build in the cloud and push
services=("booking-service" "order-service" "payment-service" "guest-service" "staff-service" "hotel-service" "restaurant-service" "notification-service" "api-gateway")

for service in "${services[@]}"; do
    echo "Building $service in Docker Cloud..."
    cd $service

    # Build the JAR first (still local, but fast)
    ./mvnw clean package -DskipTests

    # Build Docker image in the CLOUD
    docker buildx build \
        --builder cloud-builder \
        --platform linux/amd64 \
        --push \
        -t nivakaran/hotel-management-$service:latest \
        .

    cd ..
done