# Use official Node.js image
FROM node:20

# Set working directory inside container
WORKDIR /app

# Copy package files first (for caching)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy rest of the application
COPY src .

# Expose port (change if your app uses different port)
EXPOSE 3000

# Start the app
CMD ["npm", "start"]