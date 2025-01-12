import { Bill } from '@/types';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { AccordionRoot, Button, Flex, Heading } from '@chakra-ui/react';
import { FC, useEffect, useState } from 'react';

import { BillItem } from '../bill-item/bill-item';

type BillsGroupProps = {
  groupId: string | undefined;
};

export const BillsGroup: FC<BillsGroupProps> = ({ groupId }) => {
  const [bills, setBills] = useState<Bill[]>([]);

  const getBills = async () => {
    return apiClient
      .get(`/bills/group/${groupId}`)
      .then((response) => setBills(response.data))
      .catch((error) => {
        console.error(error);
      });
  };

  useEffect(() => {
    getBills();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
      <Flex bg="hover" align="center" justify="space-between">
        <Heading
          color="textRaw"
          fontSize={{ base: 'md', lg: 'xl' }}
          lineHeight={1.3}
        >
          Bills
        </Heading>
        <Button
          bg="primary"
          _hover={{ bg: 'hoverPrimary' }}
          transition={'all 0.15s ease'}
          color="textBg"
          fontSize={'md'}
        >
          + Create Bill
        </Button>
      </Flex>
      <Flex direction="column" gap={5}>
        <AccordionRoot collapsible>
          {bills.map((bill) => (
            <BillItem bill={bill} getBills={getBills} />
          ))}
        </AccordionRoot>
      </Flex>
    </>
  );
};
