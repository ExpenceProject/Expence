import { Bill, GroupMember, GroupMemberWithUser } from '@/types';
import { openBillCreationModalAtom } from '@/utils/atoms/modal-atoms';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { AccordionRoot, Button, Flex, Heading } from '@chakra-ui/react';
import { useAtom } from 'jotai';
import { FC, useCallback, useEffect, useState } from 'react';

import { BillCreationDialog } from '../bill-creation-dialog/bill-creation-dialog';
import { BillItem } from '../bill-item/bill-item';

type BillsGroupProps = {
  groupId: string | undefined;
  members: GroupMemberWithUser[];
  member: GroupMember | null;
};

export const BillsGroup: FC<BillsGroupProps> = ({
  groupId,
  members,
  member,
}) => {
  const [bills, setBills] = useState<Bill[]>([]);
  const [, openBillCreationModal] = useAtom(openBillCreationModalAtom);

  const getBills = useCallback(async (): Promise<Bill[]> => {
    return apiClient
      .get(`/bills/group/${groupId}`)
      .then((response) => {
        setBills(response.data);
        return response.data;
      })
      .catch((error) => {
        console.error(error);
        return [];
      });
  }, [groupId]);

  const handleOpenBillCreationModal = () => {
    window.scrollTo(0, 0);
    openBillCreationModal();
  };

  useEffect(() => {
    getBills();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [getBills]);

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
          onClick={handleOpenBillCreationModal}
          fontSize={'md'}
        >
          + Create Bill
        </Button>
      </Flex>
      <Flex direction="column" gap={5}>
        <AccordionRoot collapsible>
          {bills.map((bill, index) => (
            <BillItem bill={bill} getBills={getBills} key={index} />
          ))}
        </AccordionRoot>
      </Flex>

      <BillCreationDialog
        getBills={getBills}
        setBills={setBills}
        members={members}
        lender={member}
        groupId={groupId}
      />
    </>
  );
};
