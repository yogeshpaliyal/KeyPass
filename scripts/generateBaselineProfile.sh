#!/bin/bash

./gradlew :app:installFreeBenchmarkRelease

./gradlew  :baselineprofile:installFreeBenchmarkRelease

./gradlew :baselineprofile:connectedFreeBenchmarkReleaseAndroidTest -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile
adb pull /sdcard/Android/media/com.yogeshpaliyal.baselineprofile  app/src/main/baseline-prof.txt