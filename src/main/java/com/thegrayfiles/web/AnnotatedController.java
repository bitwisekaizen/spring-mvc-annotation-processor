package com.thegrayfiles.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//@Controller
//@RequestMapping(value = "/top/level/mapping")
public class AnnotatedController {

//    @Autowired
//    private ImagesService imagesService;

    @RequestMapping(value = "/something", method = RequestMethod.GET)
    public @ResponseBody void doGetWithNoParameters() {

    }
/*

    @RequestMapping(value = IMAGES_MAPPING, method = RequestMethod.POST)
    public Image uploadImage(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return imagesService.addImage(file.getBytes());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = IMAGES_MAPPING, method = RequestMethod.DELETE)
    public void deleteImage(@RequestParam(value = "uuid") String uuid) {
        imagesService.removeImage(uuid);
    }

    @RequestMapping(value = "/method/level/mapping", method = RequestMethod.GET)
    public @ResponseBody byte[] getImageContent(@RequestParam(value = "uuid") String uuid) {
        return imagesService.getImageContent(uuid).getContent();
    }
    */
}
