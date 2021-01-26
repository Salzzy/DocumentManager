package de.format.salzzy.Rechnungsmanager.service;

import de.format.salzzy.Rechnungsmanager.model.Setting;
import de.format.salzzy.Rechnungsmanager.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceImpl implements SettingService{

    private final SettingRepository settingRepository;

    @Autowired
    public SettingServiceImpl(SettingRepository settingRepository){
        this.settingRepository = settingRepository;
    }

    @Override
    public Setting getSetting() {
        Setting setting = null;
        if (settingRepository.findById(1).isPresent()) setting = settingRepository.findById(1).get();
        return setting;
    }
}
