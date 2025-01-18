import {
  PopoverArrow,
  PopoverBody,
  PopoverContent,
  PopoverRoot,
  PopoverTitle,
  PopoverTrigger,
} from '@/components/ui/popover';
import { Payment } from '@/types';
import { apiClient } from '@/utils/http-clients/api-http-client';
import {
  AccordionItem,
  AccordionItemContent,
  AccordionItemTrigger,
  Button,
  Flex,
  HStack,
  Text,
} from '@chakra-ui/react';
import { DateTime } from 'luxon';
import { FC, useState } from 'react';
import { FaMoneyBillTransfer } from 'react-icons/fa6';
import { IoCloseOutline } from 'react-icons/io5';
import { toast } from 'react-toastify';

type PaymentItemProps = {
  payment: Payment;
  isAdmin?: boolean;
  getPayments: () => void;
};

export const PaymentItem: FC<PaymentItemProps> = ({ payment, getPayments }) => {
  const [isDeletePaymentPopoverOpen, setIsDeletePaymentPopoverOpen] =
    useState(false);

  const handlePaymentDelete = () => {
    apiClient
      .delete(`/payments/${payment.id}`)
      .then(() => {
        getPayments();
        toast.success('Payment deleted successfully');
      })
      .catch((error) => {
        console.error(error);
        toast.error(
          'Failed to delete payment, please try again in a few minutes',
        );
      })
      .finally(() => {
        setIsDeletePaymentPopoverOpen(false);
      });
  };

  return (
    <Flex align="center" justify="space-between" mb={2}>
      <AccordionItem key={payment.id} value={payment.id} w="100%">
        <AccordionItemTrigger>
          <HStack w="100%">
            <Flex align="center" justify="left" spaceX={5} w="100%">
              <FaMoneyBillTransfer />
              <Flex align="center" justify="left" color="textRaw" fontSize="md">
                {payment.sender.nickname} sent&nbsp;
                <Text fontSize="lg" fontWeight="530" color="text">
                  {payment.amount}$
                </Text>
                &nbsp;to {payment.receiver.nickname}
              </Flex>
            </Flex>
          </HStack>
        </AccordionItemTrigger>
        <AccordionItemContent>
          <Flex direction="column" p={2}>
            <Flex justify="space-between" mb={2}>
              <Flex align="center" color="textDimmed" fontSize="sm">
                Created at:{' '}
                {DateTime.fromISO(payment.createdAt).toFormat('dd/MM/yyyy')}
              </Flex>
              <Flex gap={1} align="center">
                <PopoverRoot
                  open={isDeletePaymentPopoverOpen}
                  onOpenChange={(e) => setIsDeletePaymentPopoverOpen(e.open)}
                >
                  <PopoverTrigger
                    asChild
                    onClick={() =>
                      setIsDeletePaymentPopoverOpen(!isDeletePaymentPopoverOpen)
                    }
                  >
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
                      <IoCloseOutline
                        style={{ width: '25px', height: '25px' }}
                      />
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent>
                    <PopoverArrow />
                    <PopoverBody>
                      <PopoverTitle>
                        Are you sure you want to delete this payment?
                      </PopoverTitle>
                      <Flex gap={4} pt={4}>
                        <Button
                          flex={1}
                          color="textError"
                          _hover={{ bg: 'backgroundErrorHover' }}
                          bgColor="backgroundError"
                          fontSize={{ base: 'sm', md: 'md' }}
                          onClick={handlePaymentDelete}
                        >
                          Yes
                        </Button>
                        <Button
                          flex={1}
                          color="textBg"
                          _hover={{ bg: 'hoverPrimary' }}
                          bgColor="primary"
                          fontSize={{ base: 'sm', md: 'md' }}
                          onClick={() => setIsDeletePaymentPopoverOpen(false)}
                        >
                          Cancel
                        </Button>
                      </Flex>
                    </PopoverBody>
                  </PopoverContent>
                </PopoverRoot>
              </Flex>
            </Flex>
          </Flex>
        </AccordionItemContent>
      </AccordionItem>
    </Flex>
  );
};
