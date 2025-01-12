import { GroupMember, GroupMemberWithUser, Payment } from '@/types';
import { openPaymentCreationModalAtom } from '@/utils/atoms/modal-atoms';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { AccordionRoot, Button, Flex, Heading } from '@chakra-ui/react';
import { useAtom } from 'jotai';
import { FC, useCallback, useEffect, useState } from 'react';

import { PaymentCreationDialog } from '../payment-creation-dialog/payment-creation-dialog';
import { PaymentItem } from '../payment-item/payment-item';

type PaymentsGroupProps = {
  groupId: string | undefined;
  members: GroupMemberWithUser[];
  owner: GroupMember | null;
};

export const PaymentsGroup: FC<PaymentsGroupProps> = ({
  groupId,
  members,
  owner,
}) => {
  const [payments, setPayments] = useState<Payment[]>([]);
  const [, openPaymentCreationModal] = useAtom(openPaymentCreationModalAtom);

  const getPayments = useCallback(async (): Promise<Payment[]> => {
    return apiClient
      .get(`/payments/group/${groupId}`)
      .then((response) => {
        setPayments(response.data);
        return response.data;
      })
      .catch((error) => {
        console.error(error);
        return [];
      });
  }, [groupId]);

  const handleOpenPaymentCreationModal = () => {
    window.scrollTo(0, 0);
    openPaymentCreationModal();
  };

  useEffect(() => {
    getPayments();
  }, [getPayments]);
  return (
    <>
      <Flex bg="hover" align="center" justify="space-between">
        <Heading
          color="textRaw"
          fontSize={{ base: 'md', lg: 'xl' }}
          lineHeight={1.3}
        >
          Payments
        </Heading>
        <Button
          bg="primary"
          _hover={{ bg: 'hoverPrimary' }}
          transition={'all 0.15s ease'}
          color="textBg"
          onClick={handleOpenPaymentCreationModal}
          fontSize={'md'}
        >
          + Create Payment
        </Button>
      </Flex>
      <Flex direction="column" gap={5}>
        <AccordionRoot collapsible>
          {payments.map((payment) => (
            <PaymentItem
              key={payment.id}
              payment={payment}
              getPayments={getPayments}
            />
          ))}
        </AccordionRoot>
      </Flex>
      <PaymentCreationDialog
        getPayments={getPayments}
        setPayments={setPayments}
        members={members}
        owner={owner}
        groupId={groupId}
      />
    </>
  );
};
