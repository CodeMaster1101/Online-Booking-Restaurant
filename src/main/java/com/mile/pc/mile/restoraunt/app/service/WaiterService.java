package com.mile.pc.mile.restoraunt.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mile.pc.mile.restoraunt.app.constants.CONSTANTS;
import com.mile.pc.mile.restoraunt.app.exceptions.AlreadyBusyException;
import com.mile.pc.mile.restoraunt.app.model.Reservation;
import com.mile.pc.mile.restoraunt.app.model.RestorauntTable;
import com.mile.pc.mile.restoraunt.app.model.User;
import com.mile.pc.mile.restoraunt.app.repo.ReservationRepository;
import com.mile.pc.mile.restoraunt.app.repo.RoleRepository;
import com.mile.pc.mile.restoraunt.app.repo.TableRepository;
import com.mile.pc.mile.restoraunt.app.repo.UserRepository;

import lombok.SneakyThrows;

@Service  
public class WaiterService {

	@Autowired RoleRepository roleRepo;
	@Autowired TableRepository tRepo;
	@Autowired ReservationRepository reservations;
	@Autowired UserRepository urepo;

	@SneakyThrows @Transactional
	public void setBusy(long tableid) {
		RestorauntTable table = tRepo.findById(tableid).get();
		if(table.getBusy()!=true) {
			Reservation currentRes = findCurrentReservation(table);
			reservationBusyLogic(currentRes);
			table.setBusy(true);
			return;
		}
		throw new AlreadyBusyException(tableid);
	}

	@Transactional
	public void setCalm(long tableid) {
		RestorauntTable table = tRepo.findById(tableid).get();
		emptyTableAndReservations(table, reservations.findAll().stream()
				.filter(res -> res.getTable().equals(table) && res.isBusy()).findFirst().get());
	}

	public List<Reservation> todayReservations(){
		return reservations.findAll().stream()
				.filter(r -> r.getTime().getDayOfMonth() == LocalDate.now().getDayOfMonth())
				.collect(Collectors.toList());
	}

	@Transactional
	public int removeExpiredReservations() {
		int i = 0;
		List<Reservation> expiredReservations = reservations.findAll().stream()
				.filter(r ->  r.isBusy() == false && r.getTime().plusMinutes(CONSTANTS.AFTER_RESERVATION_TIME).isBefore(LocalDateTime.now())).collect(Collectors.toList());
		if(expiredReservations.isEmpty() == false) {
			for (Reservation reservation : expiredReservations) {
				sendMoneyToAdmin(reservation);
				i++;
			}
		}return i;
	}

	private void removeReservation(long id) {
		Reservation reservation = reservations.findById(id).get();
		reservation.getUser().setReservation(null);
		reservation.getUser().setReservationMoment(null);
		reservation.getTable().removeReservation(reservation);
	}

	private void sendMoneyToAdmin(Reservation reservation) {
		if(reservation.getFee() != null) {
			User admin = urepo.findAll().stream().filter(u -> u.getRoles().contains(
					roleRepo.findByType("ADMIN"))).findFirst().get();
			admin.setBalance(admin.getBalance() + reservation.getFee());
		}
		removeReservation(reservation.getId());
	}

	private Reservation findCurrentReservation(RestorauntTable table) {
		LocalTime now = LocalTime.now();
		return table.getReservations().stream()
				.filter(r -> 
				r.getTime().getDayOfMonth() == LocalDateTime.now().getDayOfMonth() &&
				now.isAfter(r.getTime().toLocalTime().minusMinutes(CONSTANTS.BEFORE_RESERVATION_TIME))
				&&  now.isBefore(r.getTime().toLocalTime().plusMinutes((CONSTANTS.AFTER_RESERVATION_TIME)))).findFirst().get();
	}

	private void emptyTableAndReservations(RestorauntTable table, Reservation currentReservation) {
		removeReservation(currentReservation.getId());		
		table.setBusy(false);
	}

	private void reservationBusyLogic(Reservation reservation) {
		//keeps the reservation as a "busy reservation" in a map to ease the complexity, later implemented in the setCalm(long id) method
		reservation.getUser().setBalance(reservation.getUser().getBalance() + reservation.getFee());
		reservation.setFee(null);
		reservation.setBusy(true);

	}

}
