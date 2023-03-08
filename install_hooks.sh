#!/usr/bin/bash
# Registers the Git Hooks provided with the Repository.

ALL_MATCHER=/*

REPO_HOOK_DIR=$(git rev-parse --show-toplevel)/.githooks
REPO_HOOKS_MATCHER=$REPO_HOOK_DIR$ALL_MATCHER

GIT_HOOK_DIR=$(git rev-parse --show-toplevel)/.git/hooks

# Copy all Custom Hooks and ensure that they stay Executable
cp -p $REPO_HOOKS_MATCHER $GIT_HOOK_DIR
