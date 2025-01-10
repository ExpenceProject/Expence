import { Bill } from '@/types';
import {
  AccordionItem,
  AccordionItemContent,
  AccordionItemTrigger,
  Button,
  Flex,
  HStack,
} from '@chakra-ui/react';
import { FC } from 'react';
import { FiEdit } from 'react-icons/fi';

type BillItemProps = {
  bill: Bill;
};

export const BillItem: FC<BillItemProps> = ({ bill }) => {
  return (
    <Flex align="center" justify="space-between" mb={2}>
      <AccordionItem key={bill.id} value={bill.id} w="100%">
        <AccordionItemTrigger>
          <HStack w="100%">
            <Flex align="center" justify="space-between" w="100%">
              <Flex align="center" color="textRaw" fontSize="lg">
                {bill.name}
              </Flex>
              <Flex gap={2}>
                <Flex
                  align="center"
                  color="textDimmed"
                  fontSize="md"
                  justify="center"
                >
                  {bill.totalAmount}$ paid by {bill.lender.nickname}
                </Flex>
                <Flex align="center" justify="flex-end">
                  <Button
                    bg="none"
                    color="text"
                    minW={0}
                    w={8}
                    h={8}
                    _hover={{ bg: 'disabled' }}
                    p={0}
                    mt={-1}
                  >
                    <FiEdit style={{ width: '20px', height: '20px' }} />
                  </Button>
                </Flex>
              </Flex>
            </Flex>
          </HStack>
        </AccordionItemTrigger>
        <AccordionItemContent>
          <Flex direction="column" p={2}>
            <Flex justify="space-between" mb={2}>
              <Flex align="center" color="textDimmed" fontSize="sm">
                Created at: {new Date(bill.createdAt).toLocaleDateString()}
              </Flex>
            </Flex>
            {bill.expenses.map((expense, index) => (
              <Flex key={index} justify="space-between" mb={1}>
                <Flex align="center" color="textRaw" fontSize="md">
                  {expense.borrower.nickname}
                </Flex>
                <Flex align="center" color="textDimmed" fontSize="md">
                  {expense.amount}$
                </Flex>
              </Flex>
            ))}
          </Flex>
        </AccordionItemContent>
      </AccordionItem>
    </Flex>
  );
};
