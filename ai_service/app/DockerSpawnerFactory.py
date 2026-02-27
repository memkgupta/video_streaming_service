import os
from app.local_docker_spawner import LocalDockerSpawner
# from app.spawners.ecs_spawner import ECSDockerSpawner
from app.docker_spawner import DockerSpawner


class DockerSpawnerFactory:

    @staticmethod
    def get_spawner() -> DockerSpawner:
        env = os.getenv("ENVIRONMENT", "LOCAL").upper()

        if env == "LOCAL":
            return LocalDockerSpawner()

 
        else:
            raise ValueError(f"Unsupported environment: {env}")
