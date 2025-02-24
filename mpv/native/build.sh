#!/bin/bash

platforms=("linux" "windows")
architectures=("x86_64" "aarch64")

setup() {
  builddir="build-$target"
  rm -rf "$builddir"

  meson_options=(
    "--buildtype=release"
    "--strip"
    "--force-fallback-for=ffmpeg,mpv"
  )

  if [[ ! ($OSTYPE == "linux-gnu" && $target == "linux-x86_64") ]]; then
    if [[ $platform == "android" ]]; then
      meson_options+=("--cross-file=cross/base-android.ini")
    else
      meson_options+=("--cross-file=cross/base-non-android.ini")
    fi

    meson_options+=("--cross-file=cross/$target.ini")
  fi

  meson setup "$builddir" "${meson_options[@]}"
}

compile() {
  meson compile -C "$builddir"
}

# if architecture is specificed ensure it exists, otherwise default to all
if [ -z "$1" ]; then
  echo "No target specified, defaulting to all"
  for platform in "${platforms[@]}"; do
    for architecture in "${architectures[@]}"; do
      target="$platform-$architecture"
      setup "$target"
      compile "$target"
    done
  done
else
  target=$1

  platform=$(echo "$target" | cut -d'-' -f1)
  architecture=$(echo "$target" | cut -d'-' -f2)

  # ensure the target is valid
  if [[ ! " ${platforms[*]} " =~ $platform ]]; then
    echo "Invalid platform: $platform"
    exit 1
  fi

  if [[ ! " ${architectures[*]} " =~ $architecture ]]; then
    echo "Invalid architecture: $architecture"
    exit 1
  fi

  setup "$target"
  compile "$target"
fi

if [[ -z "$2" && -d "$2" ]]; then
  echo "Collecting build artifacts"

  mkdir -p "$2"

  cp "$builddir/libmpv_jni.so" "$2"

  for lib in $(find "$(realpath "$builddir")" -type f -name "*.so.*" | grep -v ".symbols$"); do
    basename=$(basename "$lib")
    cp "$lib" "$2/${basename%.so*}.so"
  done
fi