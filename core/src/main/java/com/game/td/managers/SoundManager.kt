package com.game.td.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.TimeUtils
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object SoundManager {

    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true
    var masterVolume: Float = 1.0f
    var musicVolume: Float = 0.28f

    private val sounds = mutableMapOf<String, Sound>()
    private val lastPlayedTime = mutableMapOf<String, Long>()
    private var currentMusic: Music? = null
    private var currentMusicKey: String = ""
    private var isInitialized = false

    private const val SAMPLE_RATE = 44100
    // Dùng thư mục cache v2 để tự động tạo lại toàn bộ file âm thanh mới to và rõ hơn
    private const val CACHE_DIR = "td_audio_v2/"

    fun init() {
        if (isInitialized) return
        isInitialized = true

        try {
            // 1. UI, CHỌN THÁP, XÂY THÁP & WORLD MAP
            registerSound("ui_click", generateUiClick())
            registerSound("tower_preview", generateTowerPreview())
            registerSound("tower_build", generateTowerBuild())
            registerSound("tower_upgrade", generateTowerUpgrade())
            registerSound("tower_sell", generateCoinChime())
            registerSound("map_node", generateMapNodeSelect())
            registerSound("battle_start", generateBattleStartHorn())
            registerSound("wave_horn", generateWarHorn())
            registerSound("rally_flag", generateRallySnap())
            registerSound("life_lost", generateLifeLost())
            registerSound("victory", generateVictoryFanfare())
            registerSound("defeat", generateDefeatHorn())

            // 2. GIAO TRANH, LÍNH CHẾT, QUÁI CHẾT & VŨ KHÍ
            registerSound("arrow_shoot", generateArrowShoot())
            registerSound("musket_shot", generateMusketShot())
            registerSound("magic_bolt", generateMagicBolt())
            registerSound("artillery_fire", generateCannonFire())
            registerSound("bomb_explode", generateExplosion())
            registerSound("sword_clash", generateSwordClash())
            registerSound("enemy_death", generateEnemyDeathGroan())
            registerSound("soldier_death", generateSoldierDeathCry())

            // 3. KỸ NĂNG LEVEL 4 & PHÉP BỔ TRỢ
            registerSound("rain_of_fire", generateMeteorStrike())
            registerSound("reinforcements", generateReinforcementCall())
            registerSound("sniper_shot", generateSniperCrack())
            registerSound("forest_wrath", generateVineTrap())
            registerSound("death_ray", generateDeathRay())
            registerSound("teleport", generateTeleportWarp())
            registerSound("polymorph", generatePolymorphSheep())
            registerSound("healing_light", generateHealingChoir())
            registerSound("holy_strike", generateHolySmite())
            registerSound("whirlwind", generateWhirlwindSlash())
            registerSound("tesla_bolt", generateTeslaZap())
            registerSound("sunray_beam", generateSunrayLaser())

            // 4. NHẠC NỀN CHẠY LIÊN TỤC (LOOPING BGM)
            ensureMusicFile("bgm_map", generateMapThemePcm())
            ensureMusicFile("bgm_grass", generateBattleThemePcm(baseFreq = 110.0, isMinor = false, tempoBpm = 126))
            ensureMusicFile("bgm_snow", generateBattleThemePcm(baseFreq = 98.0, isMinor = true, tempoBpm = 120))
            ensureMusicFile("bgm_wastes", generateBattleThemePcm(baseFreq = 82.4, isMinor = true, tempoBpm = 132))
        } catch (e: Exception) {
            Gdx.app?.error("SoundManager", "Audio init error: ${e.message}")
        }
    }

    private fun registerSound(key: String, pcmData: ShortArray) {
        try {
            val customInternal = Gdx.files.internal("sounds/$key.wav")
            val handle = if (customInternal.exists()) {
                customInternal
            } else {
                val localFile = Gdx.files.local("$CACHE_DIR$key.wav")
                if (!localFile.exists() || localFile.length() < 200L) {
                    localFile.writeBytes(encodeWav(pcmData), false)
                }
                localFile
            }
            sounds[key] = Gdx.audio.newSound(handle)
        } catch (e: Exception) {
            Gdx.app?.error("SoundManager", "Failed to register sound $key: ${e.message}")
        }
    }

    private fun ensureMusicFile(key: String, pcmData: ShortArray) {
        try {
            val localFile = Gdx.files.local("$CACHE_DIR$key.wav")
            if (!localFile.exists() || localFile.length() < 1000L) {
                localFile.writeBytes(encodeWav(pcmData), false)
            }
        } catch (e: Exception) {
            Gdx.app?.error("SoundManager", "Failed to write music $key: ${e.message}")
        }
    }

    private fun play(key: String, volumeScale: Float = 1.0f, pitch: Float = 1.0f, minIntervalMs: Long = 40L) {
        if (!isInitialized) init()
        if (!isSoundEnabled) return

        val now = TimeUtils.millis()
        val last = lastPlayedTime[key] ?: 0L
        if (now - last < minIntervalMs) return
        lastPlayedTime[key] = now

        sounds[key]?.play((masterVolume * volumeScale).coerceIn(0f, 1f), pitch, 0f)
    }

    // =========================================================================
    // CÁC HÀM GỌI HIỆU ỨNG ÂM THANH (SFX) TRONG GAME
    // =========================================================================
    fun playClick() = play("ui_click", 0.90f, 1.0f, 25L)
    fun playTowerPreview() = play("tower_preview", 0.95f, Random.nextFloat() * 0.08f + 0.96f, 30L)
    fun playTowerBuild() = play("tower_build", 1.0f, 1.0f, 50L)
    fun playTowerUpgrade() = play("tower_upgrade", 1.0f, 1.0f, 50L)
    fun playTowerSell() = play("tower_sell", 1.0f, 1.0f, 50L)
    fun playMapNodeSelect() = play("map_node", 0.95f, 1.0f, 40L)
    fun playBattleStart() = play("battle_start", 1.0f, 1.0f, 150L)
    fun playWaveStart() = play("wave_horn", 1.0f, 1.0f, 200L)
    fun playRallyPoint() = play("rally_flag", 0.95f, 1.0f, 60L)
    fun playLifeLost() = play("life_lost", 1.0f, 1.0f, 120L)
    fun playVictory() = play("victory", 1.0f, 1.0f, 400L)
    fun playDefeat() = play("defeat", 1.0f, 1.0f, 400L)

    fun playArrowShoot() = play("arrow_shoot", 0.68f, Random.nextFloat() * 0.16f + 0.92f, 55L)
    fun playMusketShot() = play("musket_shot", 0.88f, Random.nextFloat() * 0.12f + 0.94f, 65L)
    fun playMagicBolt() = play("magic_bolt", 0.75f, Random.nextFloat() * 0.14f + 0.93f, 65L)
    fun playArtilleryFire() = play("artillery_fire", 0.92f, Random.nextFloat() * 0.10f + 0.95f, 80L)
    fun playBombExplosion() = play("bomb_explode", 0.95f, Random.nextFloat() * 0.14f + 0.93f, 65L)

    // Tiếng kiếm chém khiên ken keng khi lính và quái giao tranh
    fun playSwordClash() = play("sword_clash", 0.85f, Random.nextFloat() * 0.22f + 0.89f, 95L)

    // Tiếng quái chết & lính chết rõ ràng
    fun playEnemyDeath() = play("enemy_death", 0.90f, Random.nextFloat() * 0.24f + 0.88f, 55L)
    fun playSoldierDeath() = play("soldier_death", 0.95f, Random.nextFloat() * 0.14f + 0.93f, 80L)

    fun playRainOfFire() = play("rain_of_fire", 1.0f, 1.0f, 100L)
    fun playReinforcements() = play("reinforcements", 0.95f, 1.0f, 120L)
    fun playSniperShot() = play("sniper_shot", 0.95f, 1.0f, 90L)
    fun playForestWrath() = play("forest_wrath", 0.90f, 1.0f, 100L)
    fun playDeathRay() = play("death_ray", 0.95f, 1.0f, 90L)
    fun playTeleport() = play("teleport", 0.90f, 1.0f, 90L)
    fun playPolymorph() = play("polymorph", 0.95f, 1.0f, 90L)
    fun playHealingLight() = play("healing_light", 0.88f, 1.0f, 120L)
    fun playHolyStrike() = play("holy_strike", 0.92f, 1.0f, 90L)
    fun playWhirlwind() = play("whirlwind", 0.88f, Random.nextFloat() * 0.12f + 0.94f, 75L)
    fun playTeslaBolt() = play("tesla_bolt", 0.92f, Random.nextFloat() * 0.12f + 0.94f, 75L)
    fun playSunrayLaser() = play("sunray_beam", 1.0f, 1.0f, 120L)

    // =========================================================================
    // ĐIỀU KHIỂN NHẠC NỀN LIÊN TỤC (LOOPING BACKGROUND MUSIC)
    // =========================================================================
    fun playMapMusic() {
        startMusicTrack("bgm_map")
    }

    fun playLevelMusic(levelNumber: Int) {
        val key = when (levelNumber) {
            in 1..6 -> "bgm_grass"
            in 7..9 -> "bgm_snow"
            in 10..12 -> "bgm_wastes"
            else -> "bgm_grass"
        }
        startMusicTrack(key)
    }

    private fun startMusicTrack(key: String) {
        if (!isInitialized) init()
        if (!isMusicEnabled) {
            stopMusic()
            return
        }
        if (currentMusicKey == key && currentMusic?.isPlaying == true) {
            currentMusic?.volume = musicVolume
            return
        }

        try {
            currentMusic?.stop()
            currentMusic?.dispose()
            currentMusic = null

            val customInternal = Gdx.files.internal("sounds/$key.wav")
            val handle = if (customInternal.exists()) customInternal else Gdx.files.local("$CACHE_DIR$key.wav")
            if (handle.exists()) {
                currentMusic = Gdx.audio.newMusic(handle).apply {
                    isLooping = true
                    volume = musicVolume
                    play()
                }
                currentMusicKey = key
            }
        } catch (_: Exception) {}
    }

    fun setMusicPaused(paused: Boolean) {
        if (!isMusicEnabled) return
        currentMusic?.volume = if (paused) musicVolume * 0.30f else musicVolume
    }

    fun toggleAudio(): Boolean {
        isSoundEnabled = !isSoundEnabled
        isMusicEnabled = isSoundEnabled
        if (!isMusicEnabled) {
            currentMusic?.pause()
        } else {
            currentMusic?.play()
            currentMusic?.volume = musicVolume
        }
        return isSoundEnabled
    }

    fun stopMusic() {
        try {
            currentMusic?.stop()
        } catch (_: Exception) {}
    }

    fun dispose() {
        stopMusic()
        currentMusic?.dispose()
        currentMusic = null
        sounds.values.forEach { it.dispose() }
        sounds.clear()
        isInitialized = false
    }

    // =========================================================================
    // BỘ TỔNG HỢP ÂM THANH 16-BIT PCM WAV (44100 HZ, ĐẦY ĐẶN & RÕ NÉT)
    // =========================================================================
    private fun encodeWav(samples: ShortArray): ByteArray {
        val byteStream = ByteArrayOutputStream(44 + samples.size * 2)
        val out = DataOutputStream(byteStream)
        val dataSize = samples.size * 2

        out.writeBytes("RIFF")
        out.writeInt(Integer.reverseBytes(36 + dataSize))
        out.writeBytes("WAVE")
        out.writeBytes("fmt ")
        out.writeInt(Integer.reverseBytes(16))
        out.writeShort(java.lang.Short.reverseBytes(1.toShort()).toInt())
        out.writeShort(java.lang.Short.reverseBytes(1.toShort()).toInt())
        out.writeInt(Integer.reverseBytes(SAMPLE_RATE))
        out.writeInt(Integer.reverseBytes(SAMPLE_RATE * 2))
        out.writeShort(java.lang.Short.reverseBytes(2.toShort()).toInt())
        out.writeShort(java.lang.Short.reverseBytes(16.toShort()).toInt())
        out.writeBytes("data")
        out.writeInt(Integer.reverseBytes(dataSize))

        for (s in samples) {
            out.writeShort(java.lang.Short.reverseBytes(s).toInt())
        }
        out.flush()
        return byteStream.toByteArray()
    }

    private fun synth(durationSec: Float, generator: (t: Double, progress: Double) -> Double): ShortArray {
        val count = (SAMPLE_RATE * durationSec).toInt().coerceAtLeast(1)
        val out = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / count
            val v = generator(t, progress).coerceIn(-1.0, 1.0)
            out[i] = (v * 31500.0).toInt().toShort()
        }
        return out
    }

    // 1. Tiếng bấm nút UI chung (Đanh gọn, rõ ràng)
    private fun generateUiClick(): ShortArray = synth(0.12f) { t, p ->
        val env = exp(-p * 8.0)
        val click = sin(2.0 * PI * 850.0 * t) * 0.55 + sin(2.0 * PI * 1450.0 * t) * 0.45
        click * env
    }

    // 2. Tiếng chọn ô đất / chọn biểu tượng tháp để xem trước (Tiếng bánh răng sắt + chuông gỗ)
    private fun generateTowerPreview(): ShortArray = synth(0.20f) { t, p ->
        val part1 = if (t < 0.08) exp(-t * 22.0) * (sin(2.0 * PI * 480.0 * t) * 0.6 + Random.nextDouble(-0.35, 0.35)) else 0.0
        val part2 = if (t >= 0.05) exp(-(t - 0.05) * 12.0) * (sin(2.0 * PI * 920.0 * t) * 0.65 + sin(2.0 * PI * 1380.0 * t) * 0.35) else 0.0
        (part1 + part2) * (1.0 - p * 0.2)
    }

    // 3. Tiếng xây dựng tháp (3 nhịp búa đóng gỗ + đặt đá + hợp âm hoàn thành)
    private fun generateTowerBuild(): ShortArray = synth(0.62f) { t, _ ->
        val hammer1 = if (t in 0.0..0.15) {
            val lt = t
            exp(-lt * 18.0) * (sin(2.0 * PI * 160.0 * lt) * 0.70 + sin(2.0 * PI * 420.0 * lt) * 0.35 + Random.nextDouble(-0.4, 0.4) * 0.45)
        } else 0.0

        val hammer2 = if (t in 0.16..0.32) {
            val lt = t - 0.16
            exp(-lt * 18.0) * (sin(2.0 * PI * 210.0 * lt) * 0.70 + sin(2.0 * PI * 540.0 * lt) * 0.35 + Random.nextDouble(-0.4, 0.4) * 0.45)
        } else 0.0

        val finishChord = if (t >= 0.32) {
            val lt = t - 0.32
            exp(-lt * 6.5) * (
                sin(2.0 * PI * 523.25 * lt) * 0.45 +
                    sin(2.0 * PI * 659.25 * lt) * 0.35 +
                    sin(2.0 * PI * 783.99 * lt) * 0.30
                )
        } else 0.0

        hammer1 + hammer2 + finishChord
    }

    // 4. Tiếng nâng cấp tháp / nâng kỹ năng
    private fun generateTowerUpgrade(): ShortArray = synth(0.55f) { t, _ ->
        val f = when {
            t < 0.14 -> 523.25 // C5
            t < 0.28 -> 659.25 // E5
            else -> 1046.50    // C6
        }
        val localT = when {
            t < 0.14 -> t
            t < 0.28 -> t - 0.14
            else -> t - 0.28
        }
        val env = exp(-localT * 5.5)
        (sin(2.0 * PI * f * t) * 0.55 + sin(2.0 * PI * f * 1.5 * t) * 0.25 + sin(2.0 * PI * f * 2.0 * t) * 0.20) * env
    }

    // 5. Tiếng bán tháp / nhận vàng (Tiếng đồng xu leng keng)
    private fun generateCoinChime(): ShortArray = synth(0.48f) { t, _ ->
        val c1 = exp(-t * 8.0) * (sin(2.0 * PI * 1567.98 * t) * 0.5 + sin(2.0 * PI * 3135.96 * t) * 0.3)
        val c2 = if (t > 0.09) exp(-(t - 0.09) * 6.5) * (sin(2.0 * PI * 2093.0 * (t - 0.09)) * 0.55 + sin(2.0 * PI * 4186.0 * (t - 0.09)) * 0.3) else 0.0
        c1 + c2
    }

    // 6. Tiếng bấm chọn cờ màn chơi ở World Map (Tiếng tuốt kiếm / cắm cờ)
    private fun generateMapNodeSelect(): ShortArray = synth(0.30f) { t, p ->
        val freq = 420.0 + 780.0 * p
        val env = exp(-p * 4.5)
        val ring = sin(2.0 * PI * freq * t) * 0.55 + sin(2.0 * PI * freq * 1.5 * t) * 0.30
        val scrape = Random.nextDouble(-0.35, 0.35) * exp(-p * 8.0) * 0.35
        (ring + scrape) * env
    }

    // 7. Tiếng bấm nút TO BATTLE! ở World Map (Trống trận + Tù và xuất quân)
    private fun generateBattleStartHorn(): ShortArray = synth(0.75f) { t, p ->
        val drum = if (t < 0.20) exp(-t * 14.0) * (sin(2.0 * PI * 85.0 * t) * 0.7 + Random.nextDouble(-0.4, 0.4) * 0.4) else 0.0
        val horn = if (t >= 0.10) {
            val f = if (t < 0.35) 293.66 else 440.0 // D4 -> A4
            val env = (1.0 - p * 0.85)
            (sin(2.0 * PI * f * t) * 0.50 + sin(2.0 * PI * f * 2.0 * t) * 0.30 + sin(2.0 * PI * f * 3.0 * t) * 0.15) * env
        } else 0.0
        drum + horn
    }

    // 8. Tiếng kiếm chạm khiên "Ken keng!" khi lính và quái đánh nhau
    private fun generateSwordClash(): ShortArray = synth(0.36f) { t, _ ->
        // Nhịp chém 1 ("Ken!") ở 0.0s và nhịp đỡ khiên 2 ("Keng!") ở 0.14s
        val hit1 = if (t < 0.18) {
            val env1 = exp(-t * 14.0)
            val metal1 = sin(2.0 * PI * 1480.0 * t) * 0.40 +
                sin(2.0 * PI * 2340.0 * t) * 0.35 +
                sin(2.0 * PI * 3720.0 * t) * 0.25 +
                Random.nextDouble(-0.45, 0.45) * exp(-t * 28.0) * 0.40
            metal1 * env1
        } else 0.0

        val hit2 = if (t >= 0.13) {
            val lt = t - 0.13
            val env2 = exp(-lt * 11.0)
            val metal2 = sin(2.0 * PI * 1760.0 * lt) * 0.42 +
                sin(2.0 * PI * 2840.0 * lt) * 0.35 +
                sin(2.0 * PI * 4210.0 * lt) * 0.25 +
                Random.nextDouble(-0.40, 0.40) * exp(-lt * 25.0) * 0.35
            metal2 * env2
        } else 0.0

        (hit1 + hit2).coerceIn(-1.0, 1.0)
    }

    // 9. Tiếng Quái Chết ("Uaaagh!" + tiếng văng)
    private fun generateEnemyDeathGroan(): ShortArray = synth(0.38f) { t, p ->
        val freq = 280.0 - 175.0 * p + sin(2.0 * PI * 32.0 * t) * 18.0
        val env = sin((1.0 - p) * PI * 0.5) * exp(-p * 2.8)
        val voice = sin(2.0 * PI * freq * t) * 0.55 +
            sin(2.0 * PI * freq * 2.0 * t) * 0.30 +
            sin(2.0 * PI * freq * 3.0 * t) * 0.15
        val splat = if (t < 0.10) Random.nextDouble(-0.45, 0.45) * exp(-t * 22.0) * 0.40 else 0.0
        (voice * env) + splat
    }

    // 10. Tiếng Lính Mình Chết ("Aaargh!" + tiếng giáp sắt đổ sập)
    private fun generateSoldierDeathCry(): ShortArray = synth(0.46f) { t, p ->
        val freq = 390.0 - 210.0 * p + sin(2.0 * PI * 24.0 * t) * 14.0
        val env = exp(-p * 3.2)
        val cry = (sin(2.0 * PI * freq * t) * 0.50 + sin(2.0 * PI * freq * 2.0 * t) * 0.30) * env
        val armorClatter = if (t > 0.16) {
            val lt = t - 0.16
            exp(-lt * 10.0) * (sin(2.0 * PI * 1150.0 * lt) * 0.30 + sin(2.0 * PI * 1820.0 * lt) * 0.25 + Random.nextDouble(-0.4, 0.4) * 0.30)
        } else 0.0
        cry + armorClatter
    }

    private fun generateWarHorn(): ShortArray = synth(0.80f) { t, p ->
        val attack = (t / 0.08).coerceAtMost(1.0)
        val decay = (1.0 - p * 0.85).coerceIn(0.0, 1.0)
        val f = if (t < 0.30) 196.0 else 261.63
        val wave = sin(2.0 * PI * f * t) * 0.55 + sin(2.0 * PI * f * 2.0 * t) * 0.30 + sin(2.0 * PI * f * 3.0 * t) * 0.15
        wave * attack * decay
    }

    private fun generateRallySnap(): ShortArray = synth(0.26f) { t, p ->
        val env = exp(-p * 6.5)
        (Random.nextDouble(-0.5, 0.5) * 0.45 + sin(2.0 * PI * (360.0 + p * 320.0) * t) * 0.55) * env
    }

    private fun generateLifeLost(): ShortArray = synth(0.50f) { t, p ->
        val f = 210.0 - 110.0 * p
        val env = exp(-p * 3.5)
        (sin(2.0 * PI * f * t) * 0.70 + sin(2.0 * PI * f * 1.5 * t) * 0.30) * env
    }

    private fun generateVictoryFanfare(): ShortArray = synth(1.20f) { t, _ ->
        val f = when {
            t < 0.20 -> 523.25
            t < 0.40 -> 659.25
            t < 0.60 -> 783.99
            else -> 1046.50
        }
        val localT = when {
            t < 0.20 -> t
            t < 0.40 -> t - 0.20
            t < 0.60 -> t - 0.40
            else -> t - 0.60
        }
        val env = exp(-localT * 3.8)
        (sin(2.0 * PI * f * t) * 0.55 + sin(2.0 * PI * f * 1.5 * t) * 0.25 + sin(2.0 * PI * f * 2.0 * t) * 0.20) * env
    }

    private fun generateDefeatHorn(): ShortArray = synth(1.0f) { t, p ->
        val f = if (t < 0.45) 164.81 else 130.81
        val env = (1.0 - p)
        (sin(2.0 * PI * f * t) * 0.65 + sin(2.0 * PI * f * 2.0 * t) * 0.35) * env
    }

    private fun generateArrowShoot(): ShortArray = synth(0.18f) { t, p ->
        val freq = 680.0 - 420.0 * p
        val env = exp(-p * 6.5)
        (sin(2.0 * PI * freq * t) * 0.50 + Random.nextDouble(-0.5, 0.5) * 0.50) * env
    }

    private fun generateMusketShot(): ShortArray = synth(0.28f) { t, p ->
        val env = exp(-p * 8.5)
        (Random.nextDouble(-0.85, 0.85) * 0.65 + sin(2.0 * PI * (210.0 - 130.0 * p) * t) * 0.45) * env
    }

    private fun generateMagicBolt(): ShortArray = synth(0.26f) { t, p ->
        val freq = 520.0 + 540.0 * p
        val env = exp(-p * 5.5)
        (sin(2.0 * PI * freq * t) * 0.65 + sin(2.0 * PI * freq * 1.5 * t) * 0.35) * env
    }

    private fun generateCannonFire(): ShortArray = synth(0.36f) { t, p ->
        val freq = 150.0 * exp(-p * 3.5)
        val env = exp(-p * 5.5)
        (sin(2.0 * PI * freq * t) * 0.70 + Random.nextDouble(-0.5, 0.5) * 0.40) * env
    }

    private fun generateExplosion(): ShortArray = synth(0.52f) { t, p ->
        val freq = 105.0 * exp(-p * 3.0)
        val env = exp(-p * 4.5)
        (Random.nextDouble(-0.85, 0.85) * 0.65 + sin(2.0 * PI * freq * t) * 0.55) * env
    }

    private fun generateMeteorStrike(): ShortArray = synth(0.65f) { t, p ->
        val whistle = if (t < 0.25) sin(2.0 * PI * (980.0 - 620.0 * (t / 0.25)) * t) * 0.50 else 0.0
        val blast = if (t >= 0.20) exp(-(t - 0.20) * 5.0) * (Random.nextDouble(-0.85, 0.85) * 0.65 + sin(2.0 * PI * 85.0 * t) * 0.55) else 0.0
        (whistle + blast) * (1.0 - p * 0.15)
    }

    private fun generateReinforcementCall(): ShortArray = synth(0.45f) { t, _ ->
        val f = if (t < 0.20) 440.0 else 587.33
        val env = exp(-(if (t < 0.20) t else t - 0.20) * 6.5)
        (sin(2.0 * PI * f * t) * 0.65 + sin(2.0 * PI * f * 2.0 * t) * 0.35) * env
    }

    private fun generateSniperCrack(): ShortArray = synth(0.40f) { t, p ->
        val env = exp(-p * 7.5)
        (Random.nextDouble(-0.9, 0.9) * 0.60 + sin(2.0 * PI * (360.0 * exp(-p * 4.0)) * t) * 0.55) * env
    }

    private fun generateVineTrap(): ShortArray = synth(0.34f) { t, p ->
        val f = 150.0 + 190.0 * sin(p * PI * 3.0)
        exp(-p * 4.5) * (sin(2.0 * PI * f * t) * 0.55 + Random.nextDouble(-0.35, 0.35) * 0.45)
    }

    private fun generateDeathRay(): ShortArray = synth(0.48f) { t, p ->
        val f = 880.0 - 520.0 * p + 60.0 * sin(2.0 * PI * 35.0 * t)
        exp(-p * 3.8) * (sin(2.0 * PI * f * t) * 0.65 + sin(2.0 * PI * f * 2.0 * t) * 0.35)
    }

    private fun generateTeleportWarp(): ShortArray = synth(0.38f) { t, p ->
        val f = 320.0 + 760.0 * p * p
        (1.0 - p) * sin(2.0 * PI * f * t) * 0.75
    }

    private fun generatePolymorphSheep(): ShortArray = synth(0.42f) { t, p ->
        val vibrato = sin(2.0 * PI * 18.0 * t) * 28.0
        val f = 480.0 - 90.0 * p + vibrato
        val env = (1.0 - p)
        (sin(2.0 * PI * f * t) * 0.60 + sin(2.0 * PI * f * 2.0 * t) * 0.30) * env
    }

    private fun generateHealingChoir(): ShortArray = synth(0.52f) { t, p ->
        val env = (1.0 - p)
        (sin(2.0 * PI * 523.25 * t) * 0.4 + sin(2.0 * PI * 659.25 * t) * 0.35 + sin(2.0 * PI * 783.99 * t) * 0.25) * env
    }

    private fun generateHolySmite(): ShortArray = synth(0.44f) { t, p ->
        val env = exp(-p * 5.5)
        (sin(2.0 * PI * 880.0 * t) * 0.4 + sin(2.0 * PI * 440.0 * t) * 0.3 + Random.nextDouble(-0.5, 0.5) * 0.4) * env
    }

    private fun generateWhirlwindSlash(): ShortArray = synth(0.30f) { t, p ->
        val env = sin(p * PI)
        (Random.nextDouble(-0.7, 0.7) * 0.65 + sin(2.0 * PI * (280.0 + 260.0 * p) * t) * 0.35) * env
    }

    private fun generateTeslaZap(): ShortArray = synth(0.36f) { t, p ->
        val f = 420.0 + 280.0 * sin(2.0 * PI * 45.0 * t)
        val env = exp(-p * 5.0)
        (sin(2.0 * PI * f * t) * 0.55 + Random.nextDouble(-0.6, 0.6) * 0.45) * env
    }

    private fun generateSunrayLaser(): ShortArray = synth(0.70f) { t, p ->
        val f = 660.0 - 240.0 * p + 40.0 * sin(2.0 * PI * 28.0 * t)
        val env = exp(-p * 3.2)
        (sin(2.0 * PI * f * t) * 0.55 + sin(2.0 * PI * f * 1.5 * t) * 0.30 + Random.nextDouble(-0.3, 0.3) * 0.25) * env
    }

    private fun generateMapThemePcm(): ShortArray {
        val melody = doubleArrayOf(
            261.63, 329.63, 392.00, 523.25,
            293.66, 349.23, 440.00, 587.33,
            329.63, 392.00, 493.88, 659.25,
            293.66, 392.00, 493.88, 523.25
        )
        val noteDur = 0.34
        val totalDur = (melody.size * noteDur).toFloat()
        return synth(totalDur) { t, _ ->
            val idx = ((t / noteDur).toInt()).coerceIn(0, melody.size - 1)
            val localT = t - idx * noteDur
            val f = melody[idx]
            val bassF = f * 0.5
            val noteEnv = exp(-localT * 5.0)
            val lead = (sin(2.0 * PI * f * t) * 0.40 + sin(2.0 * PI * f * 2.0 * t) * 0.18) * noteEnv
            val bass = sin(2.0 * PI * bassF * t) * 0.25 * exp(-localT * 2.5)
            lead + bass
        }
    }

    private fun generateBattleThemePcm(baseFreq: Double, isMinor: Boolean, tempoBpm: Int): ShortArray {
        val beatDur = 60.0 / tempoBpm
        val stepDur = beatDur * 0.5
        val ratios = if (isMinor) {
            doubleArrayOf(
                1.0, 1.2, 1.5, 1.2, 1.0, 1.5, 1.333, 1.2,
                0.89, 1.0, 1.333, 1.2, 1.0, 1.5, 1.6, 1.5
            )
        } else {
            doubleArrayOf(
                1.0, 1.25, 1.5, 1.25, 1.333, 1.5, 1.667, 1.5,
                1.125, 1.333, 1.5, 1.25, 1.0, 1.5, 1.25, 1.0
            )
        }
        val totalSteps = ratios.size
        val totalDur = (totalSteps * stepDur).toFloat()

        return synth(totalDur) { t, _ ->
            val stepIdx = ((t / stepDur).toInt()).coerceIn(0, totalSteps - 1)
            val localT = t - stepIdx * stepDur
            val f = baseFreq * 2.0 * ratios[stepIdx]
            val bassF = baseFreq * (if (stepIdx % 4 == 0) 1.0 else 1.5)

            val leadEnv = exp(-localT * 7.0)
            val lead = (sin(2.0 * PI * f * t) * 0.32 + sin(2.0 * PI * f * 2.0 * t) * 0.15) * leadEnv
            val bass = sin(2.0 * PI * bassF * t) * 0.26 * exp(-localT * 4.0)
            val drum = if (stepIdx % 2 == 0 && localT < 0.08) {
                exp(-localT * 30.0) * (sin(2.0 * PI * 75.0 * localT) * 0.35 + Random.nextDouble(-0.2, 0.2))
            } else 0.0

            (lead + bass + drum).coerceIn(-0.90, 0.90)
        }
    }
}
