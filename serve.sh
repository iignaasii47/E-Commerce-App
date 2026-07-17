#!/usr/bin/env bash
set -euo pipefail

if ! command -v docker &>/dev/null; then
    echo "ERROR: Docker is not installed or not in PATH."
    exit 1
fi

if ! docker info &>/dev/null; then
    echo "Docker daemon is not running. Attempting to start Docker..."

    sudo_cmd=()
    if [[ "$(id -u)" -ne 0 ]]; then
        sudo_cmd=(sudo)
    fi

    if command -v systemctl &>/dev/null; then
        "${sudo_cmd[@]}" systemctl start docker
    elif command -v service &>/dev/null; then
        "${sudo_cmd[@]}" service docker start
    else
        echo "ERROR: Could not find systemctl or service to start Docker."
        exit 1
    fi

    for _ in {1..30}; do
        if docker info &>/dev/null; then
            break
        fi
        sleep 1
    done

    if ! docker info &>/dev/null; then
        echo "ERROR: Docker daemon did not start."
        exit 1
    fi
fi

docker compose up --build
