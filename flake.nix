{
  description = "Android development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs }:
  let
    system = "x86_64-linux";
    pkgs = import nixpkgs { inherit system; config.allowUnfree = true; };
  in {
    devShells.${system}.default = pkgs.mkShell {
      buildInputs = with pkgs; [
        jdk17
        gradle
        android-tools
        android-studio
      ];

      shellHook = ''
        export ANDROID_HOME=$HOME/Android/Sdk
        export JAVA_HOME=${pkgs.jdk17}
      '';
    };
  };
}
