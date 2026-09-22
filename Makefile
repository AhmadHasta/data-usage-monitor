.PHONY: help build-release release build-debug debug bundle-release clean install-release install-debug

GRADLEW = ./gradlew
RELEASE_APK = app/build/outputs/apk/release/app-release.apk
DEBUG_APK = app/build/outputs/apk/debug/app-debug.apk

help: ## Menampilkan daftar perintah yang tersedia
	@echo "Perintah yang tersedia di Makefile:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}'

build-release: ## Build APK Release (R8 minified & shrink resources)
	$(GRADLEW) assembleRelease
	@echo "\n✅ APK Release berhasil dibuat: $(RELEASE_APK)"

release: build-release ## Alias untuk build-release

build-debug: ## Build APK Debug
	$(GRADLEW) assembleDebug
	@echo "\n✅ APK Debug berhasil dibuat: $(DEBUG_APK)"

debug: build-debug ## Alias untuk build-debug

bundle-release: ## Build Android App Bundle (.aab) untuk Play Store
	$(GRADLEW) bundleRelease
	@echo "\n✅ AAB Release berhasil dibuat di app/build/outputs/bundle/release/"

install-release: build-release ## Build dan install APK Release ke perangkat via adb
	adb install -r $(RELEASE_APK)

install-debug: build-debug ## Build dan install APK Debug ke perangkat via adb
	adb install -r $(DEBUG_APK)

clean: ## Bersihkan cache dan folder output build
	$(GRADLEW) clean
