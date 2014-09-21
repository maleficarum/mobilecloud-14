package org.magnum.mobilecloud.video

import org.magnum.mobilecloud.video.repository.Video
import org.magnum.mobilecloud.video.repository.VideoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import retrofit.http.Query

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.security.Principal
import java.util.concurrent.atomic.AtomicLong;

/**
 * Date: 19/09/14 01:36
 * @author Oscar I. Hernandez
 */
@Controller
class MyController {

    @Autowired
    private VideoRepository videoRepository;
    def likesUser = [:]

    @RequestMapping(value = "/video", method = RequestMethod.POST)
    public @ResponseBody Video postVideo(@RequestBody Video video){
        videoRepository.save(video);
    }

    @RequestMapping(value = "/video", method = RequestMethod.GET)
    public @ResponseBody List getVideo(){
        def videos = videoRepository.findAll()
        videos.each {
            println(it)
        }
        videos
    }

    @RequestMapping(value = "/video/search/findByName", method = RequestMethod.GET)
    public @ResponseBody List findByTitle(@RequestParam("title") title){
        println("BUSCANDO ${title}")
        def videos = videoRepository.findByName(title)
        println("VIDEOS ${videos}")
        videos
    }

    @RequestMapping(value = "/video/{id}/like", method = RequestMethod.POST)
    public void likeVideo(@PathVariable("id") long id, HttpServletRequest request, HttpServletResponse response, Principal user) {
        def video = videoRepository.findById(id)

        if(!video) {
            response.status = 404
        } else {
            //Validar si el usuario ya hizo like al video
            def usuarios = likesUser[id]

            if(!usuarios) {
                usuarios = []
                likesUser[id] = usuarios
            }

            def usuario

            usuarios.each {
                if(it.equals(user.name)) {
                    usuario = it
                }
            }

            if(!usuario) {
                video.likes++
                videoRepository.save(video)

                likesUser[id] << user.name

                response.status = 200
            } else {
                response.status = 400
            }
        }
    }

    @RequestMapping(value = "/video/{id}", method = RequestMethod.GET)
    public @ResponseBody Video getVideoById(@PathVariable("id") long id, HttpServletRequest request, HttpServletResponse response){
        def video = videoRepository.findById(id)

        if(!video) {
            response.status = 404
        }

        video
    }

    @RequestMapping(value = "/video/{id}/likedby", method = RequestMethod.GET)
    public @ResponseBody List getUsersWhoLikedVideo(@PathVariable("id") long id, HttpServletResponse response) {
        def video = videoRepository.findById(id)
        def users

        if(!video) {
            response.status = 404
        } else {
            users = likesUser[id]
        }
        users
    }

    @RequestMapping(value = "/video/{id}/unlike", method = RequestMethod.POST)
    public void unlikeVideo(@PathVariable("id") long id, HttpServletResponse response, Principal principal) {
         def video = videoRepository.findById(id)

        if(!video) {
            response.status = 404
        } else {
            def users = likesUser[video.id]
            def user

            users.each {
                if(it.equals(principal.name)) {
                    user = it
                }
            }

            if(!user) {
                response.status = 400
            } else {
                //Remover like del usuario
                users.remove(user)
                response.status = 200
                video.likes--
                videoRepository.save(video)
            }
        }

    }

    @RequestMapping(value = "/video/search/findByDurationLessThan")
    public @ResponseBody List findByDurationLessThan(@RequestParam("duration") long duration) {
        videoRepository.findByDurationLessThan(duration)
    }

}
