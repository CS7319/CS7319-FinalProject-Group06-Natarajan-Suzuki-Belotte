package com.CS7319.Group06.eventual.service.impl;

import com.CS7319.Group06.eventual.dao.NotificationDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Notification;
import com.CS7319.Group06.eventual.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Implementation for NotificationService
 *
 * @author harininatarajan
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;

    public NotificationServiceImpl(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    @Override
    public List<Notification> getNotificationsForUser(String email, int page, int size) {
        try {
            return notificationDao.getNotificationsForUser(email, page, size);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public long countUnread(String email) {
        try {
            return notificationDao.countUnread(email);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Notification markAsRead(int id, String email) {
        try {
            Notification notification = notificationDao.markAsRead(id, email);
            if (notification == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
            }
            return notification;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void markAllAsRead(String email) {
        try {
            notificationDao.markAllAsRead(email);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void deleteNotification(int id, String email) {
        try {
            int rows = notificationDao.deleteById(id, email);
            if (rows == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
