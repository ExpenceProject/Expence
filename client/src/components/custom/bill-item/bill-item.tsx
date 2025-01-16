import {
  PopoverArrow,
  PopoverBody,
  PopoverCloseTrigger,
  PopoverContent,
  PopoverRoot,
  PopoverTitle,
  PopoverTrigger,
} from '@/components/ui/popover';
import { Bill } from '@/types';
import { apiClient } from '@/utils/http-clients/api-http-client';
import {
  AccordionItem,
  AccordionItemContent,
  AccordionItemTrigger,
  Button,
  Flex,
  HStack,
} from '@chakra-ui/react';
import { DateTime } from 'luxon';
import { FC, useState } from 'react';
import { FiEdit } from 'react-icons/fi';
import { IoCloseOutline } from 'react-icons/io5';
import { toast } from 'react-toastify';

type BillItemProps = {
  bill: Bill;
  getBills: () => void;
};

export const BillItem: FC<BillItemProps> = ({ bill, getBills }) => {
  const [isDeleteBillPopoverOpen, setIsDeleteBillPopoverOpen] = useState(false);
  const handleBillDelete = () => {
    apiClient
      .delete(`/bills/${bill.id}`)
      .then(() => {
        getBills();
        toast.info(`Bill "${bill.name}" deleted successfully`);
      })
      .catch((error) => {
        console.error(error);
        toast.error('Failed to delete bill, please try again in a few minutes');
      })
      .finally(() => {
        setIsDeleteBillPopoverOpen(false);
      });
  };

  return (
    <Flex align="center" justify="space-between" mb={2}>
      <AccordionItem value={bill.id} w="100%">
        <AccordionItemTrigger>
          <HStack w="100%">
            <Flex align="center" justify="space-between" w="100%" pr={2}>
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
              </Flex>
            </Flex>
          </HStack>
        </AccordionItemTrigger>
        <AccordionItemContent>
          <Flex direction="column" p={2}>
            <Flex justify="space-between" mb={2}>
              <Flex align="center" color="textDimmed" fontSize="sm">
                Created at:{' '}
                {DateTime.fromISO(bill.createdAt).toFormat('dd/MM/yyyy')}
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
      <Flex gap={2}>
        <PopoverRoot>
          <PopoverTrigger asChild>
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
          </PopoverTrigger>
          <PopoverContent>
            <PopoverArrow position={'top-end'} />
            <PopoverBody></PopoverBody>
            <PopoverCloseTrigger />
          </PopoverContent>
        </PopoverRoot>
        <PopoverRoot
          open={isDeleteBillPopoverOpen}
          onOpenChange={(e) => setIsDeleteBillPopoverOpen(e.open)}
        >
          <PopoverTrigger asChild>
            <Button
              bg="none"
              color="textError"
              minW={0}
              w={8}
              h={8}
              _hover={{ bg: 'backgroundErrorHover' }}
              p={0}
              mt={-1}
            >
              <IoCloseOutline style={{ width: '25px', height: '25px' }} />
            </Button>
          </PopoverTrigger>
          <PopoverContent>
            <PopoverArrow />
            <PopoverBody>
              <PopoverTitle>
                Are you sure you want to remove this bill from the group?
              </PopoverTitle>
              <Flex gap={4} pt={4}>
                <Button
                  flex={1}
                  color="textError"
                  _hover={{ bg: 'backgroundErrorHover' }}
                  bgColor="backgroundError"
                  fontSize={{ base: 'sm', md: 'md' }}
                  onClick={handleBillDelete}
                >
                  Yes
                </Button>
                <Button
                  flex={1}
                  color="textBg"
                  _hover={{ bg: 'hoverPrimary' }}
                  bgColor="primary"
                  fontSize={{ base: 'sm', md: 'md' }}
                  onClick={() => setIsDeleteBillPopoverOpen(false)}
                >
                  Cancel
                </Button>
              </Flex>
            </PopoverBody>
          </PopoverContent>
        </PopoverRoot>
      </Flex>
    </Flex>
  );
};
