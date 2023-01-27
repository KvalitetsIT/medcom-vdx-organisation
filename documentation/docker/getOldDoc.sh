#!/bin/bash

if docker pull kvalitetsit/medcom-vdx-organisation-documentation:latest; then
    echo "Copy from old documentation image."
    docker cp $(docker create kvalitetsit/medcom-vdx-organisation-documentation:latest):/usr/share/nginx/html target/old
fi
