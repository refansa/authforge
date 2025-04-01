package com.thumbleweed.authforge.core;

import com.thumbleweed.authforge.AuthForge;
import com.thumbleweed.authforge.core.datastore.DataStorePlayer;
import com.thumbleweed.authforge.core.datastore.DataStorePlayerImpl;
import com.thumbleweed.authforge.core.datastore.DataStoreStrategy;
import com.thumbleweed.authforge.core.exception.*;
import com.thumbleweed.authforge.core.validator.ChangePasswordValidator;
import com.thumbleweed.authforge.core.validator.LoginValidator;
import com.thumbleweed.authforge.core.validator.RegisterValidator;
import com.thumbleweed.authforge.core.validator.Validator;

public class DataStoreGuard implements Guard {
    private final DataStoreStrategy dataStore;

    public DataStoreGuard(DataStoreStrategy dataSourceStrategy) {
        this.dataStore = dataSourceStrategy;
    }

    @Override
    public boolean authenticate(Payload payload) throws AuthForgeException {
        Validator validator = new LoginValidator();
        validator.validate(payload);
        DataStorePlayer foundPlayer = this.dataStore.findByUsername(payload.getPlayer().getUsername().toLowerCase());
        if (foundPlayer == null) throw new PlayerNotFoundException();
        if (foundPlayer.isBanned()) {
            throw new BannedPlayerException();
        }
        String password = payload.getArgs()[payload.getArgs().length - 1];
        if (!this.dataStore.getPasswordHash().compare(foundPlayer.getPassword(), password)) {
            throw new WrongPasswordException();
        }
        return true;
    }

    @Override
    public boolean register(Payload payload) throws AuthForgeException {
        AuthForge.LOGGER.info("register init");
        Validator validator = new RegisterValidator();
        AuthForge.LOGGER.info("register validator");
        validator.validate(payload);
        AuthForge.LOGGER.info("register playerProxy");
        DataStorePlayer playerProxy = new DataStorePlayerImpl(payload.getPlayer());
        AuthForge.LOGGER.info("register check exist");
        if (this.dataStore.isExist(playerProxy)) throw new PlayerAlreadyExistException();
        AuthForge.LOGGER.info("register hash password");
        this.hashPassword(playerProxy, payload.getArgs()[payload.getArgs().length - 1]);
        AuthForge.LOGGER.info("register add data");
        return this.dataStore.add(playerProxy);
    }

    @Override
    public boolean updatePassword(Payload payload) throws AuthForgeException {
        Validator validator = new ChangePasswordValidator();
        validator.validate(payload);
        DataStorePlayer foundPlayer = this.dataStore.findByUsername(payload.getPlayer().getUsername().toLowerCase());
        if (foundPlayer == null) return false;
        if (!this.dataStore.getPasswordHash().compare(foundPlayer.getPassword(), payload.getArgs()[0]))
            throw new WrongPasswordException();

        this.hashPassword(foundPlayer, payload.getArgs()[payload.getArgs().length - 1]);

        return this.dataStore.updatePassword(foundPlayer);
    }

    private void hashPassword(DataStorePlayer player, String password) {
        player.setPassword(this.dataStore.getPasswordHash().hash(password));
    }
}