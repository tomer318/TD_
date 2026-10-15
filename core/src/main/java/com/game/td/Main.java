package com.game.td;

import com.badlogic.gdx.Game;
import com.game.td.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        // Khởi động từ màn hình chính MainMenu thay vì nhảy thẳng vào PlayScreen
        setScreen(new MainMenuScreen(this));
    }
}
