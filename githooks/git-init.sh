#!/bin/sh

echo "Setting Git hooks"
chmod -R +x ./githooks/
git config core.hooksPath ./githooks/
