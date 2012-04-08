package harvard.service;

import harvard.dao.ImagesDao;
import harvard.marshallable.Image;
import harvard.marshallable.ImageContent;
import harvard.marshallable.Images;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@Transactional
@Component
public class ImagesService {

    @Autowired
    private ImagesDao imagesDao;

    @Transactional(readOnly = true)
    public Images getAllImages() {
        return new Images(imagesDao.getAllImages());
    }

    public Image addImage(byte[] bytes) {
        return new Image(imagesDao.addImage(bytes));
    }

    public void removeImage(String uuid) {
        imagesDao.deleteImage(uuid);
    }

    @Transactional(readOnly = true)
    public ImageContent getImageContent(String uuid) {
        return new ImageContent(imagesDao.getImageContent(uuid));
    }
}