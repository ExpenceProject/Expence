import { Payment } from '@/types';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { AccordionRoot, Button, Flex, Heading } from '@chakra-ui/react';
import { FC, useCallback, useEffect, useState } from 'react';

import { PaymentItem } from '../payment-item/payment-item';

type PaymentsGroupProps = {
  groupId: string | undefined;
};

export const PaymentsGroup: FC<PaymentsGroupProps> = ({ groupId }) => {
  const [payments, setPayments] = useState<Payment[]>([]);

  const getPayments = useCallback(async () => {
    return apiClient
      .get(`/payments/group/${groupId}`)
      .then((response) => setPayments(response.data))
      .catch((error) => {
        console.error(error);
      });
  }, [groupId]);

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
    </>
  );
};
