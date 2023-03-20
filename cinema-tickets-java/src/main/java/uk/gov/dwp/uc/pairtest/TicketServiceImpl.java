package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.INFANT;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.CHILD;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private TicketPaymentService ticketPaymentService;
    private SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService ) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) throw new InvalidPurchaseException();

        if (accountId <= 0) throw new InvalidPurchaseException();

        List<TicketTypeRequest> ticketRequests = Arrays.asList(ticketTypeRequests).stream()
            .filter(ticket -> ticket != null).collect(Collectors.toList());

        if(!containsAdult(ticketRequests)) throw new InvalidPurchaseException();

        if(containsInvalidValidTicketValues(ticketRequests)) throw new InvalidPurchaseException();

        List<TicketTypeRequest> adultTickets = ticketRequests.stream().filter(ticket -> ticket.getTicketType().equals(ADULT)).collect(Collectors.toList());
        List<TicketTypeRequest> childTickets = ticketRequests.stream().filter(ticket -> ticket.getTicketType().equals(CHILD)).collect(Collectors.toList());
        List<TicketTypeRequest> infantTickets = ticketRequests.stream().filter(ticket -> ticket.getTicketType().equals(INFANT)).collect(Collectors.toList());

        int numberOfInfantTickets = infantTickets.stream().reduce(0, (infants, ticket) -> infants + ticket.getNoOfTickets(), Integer::sum);
        int numberOfAdultTickets = adultTickets.stream().reduce(0, (adults, ticket) -> adults + ticket.getNoOfTickets(), Integer::sum);
        int numberOfChildTickets = childTickets.stream().reduce(0, (children, ticket) -> children + ticket.getNoOfTickets(), Integer::sum);

        if (numberOfInfantTickets > numberOfAdultTickets) throw new InvalidPurchaseException();

        int totalNumberOfTickets = numberOfAdultTickets + numberOfChildTickets;

        if (totalNumberOfTickets > 20) throw new InvalidPurchaseException();

        int amountToPay = (numberOfAdultTickets * 20) + (numberOfChildTickets * 10);

        ticketPaymentService.makePayment(accountId, amountToPay);
        seatReservationService.reserveSeat(accountId, totalNumberOfTickets);
    }

    private boolean containsAdult(List<TicketTypeRequest> ticketTypeRequests) {
        return ticketTypeRequests.stream().anyMatch(ticket -> ticket.getTicketType().equals(ADULT));
    }
    
    private boolean containsInvalidValidTicketValues(List<TicketTypeRequest> ticketTypeRequests)  {
        return ticketTypeRequests.stream().anyMatch(ticket -> ticket.getNoOfTickets() <= 0);
    }
    
}
