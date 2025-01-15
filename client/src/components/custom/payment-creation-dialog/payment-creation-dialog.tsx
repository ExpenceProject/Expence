import { useColorModeValue } from '@/components/ui/color-mode';
import { GroupMember, GroupMemberWithUser, Payment } from '@/types';
import {
  closePaymentCreationModalAtom,
  isPaymentCreationModalOpenAtom,
} from '@/utils/atoms/modal-atoms';
import { apiClient } from '@/utils/http-clients/api-http-client';
import {
  FormControl,
  FormErrorMessage,
  FormLabel,
} from '@chakra-ui/form-control';
import {
  Button,
  DialogBackdrop,
  DialogBody,
  DialogCloseTrigger,
  DialogContent,
  DialogHeader,
  DialogRoot,
  DialogTitle,
  Flex,
  Input,
} from '@chakra-ui/react';
import { useAtom } from 'jotai';
import { Dispatch, FC, SetStateAction, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import Select from 'react-select';
import { toast } from 'react-toastify';
import styled from 'styled-components';

type PaymentCreationDialogProps = {
  getPayments: () => Promise<Payment[]>;
  setPayments: Dispatch<SetStateAction<Payment[]>>;
  members: GroupMemberWithUser[];
  owner: GroupMemberWithUser | null;
  groupId: string | undefined;
};

const StyledForm = styled('form')`
  width: 100%;
`;

export const PaymentCreationDialog: FC<PaymentCreationDialogProps> = ({
  getPayments,
  setPayments,
  members,
  owner,
  groupId,
}) => {
  const [isPaymentCreationModalOpen] = useAtom(isPaymentCreationModalOpenAtom);
  const [, closePaymentCreationModal] = useAtom(closePaymentCreationModalAtom);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedMember, setSelectedMember] = useState<Option | null>(null);

  const currentTheme = useColorModeValue('light', 'dark');

  type Option = {
    value: GroupMemberWithUser;
    label: string;
  };

  const memberOptions = useMemo(() => {
    return members.map((member) => {
      return { label: member.nickname, value: member };
    });
  }, [members]);

  interface FormValues {
    amount: string;
    receiver: GroupMember | null;
  }

  const {
    handleSubmit,
    register,
    reset,
    setValue,
    setError,
    clearErrors,
    formState: { errors },
  } = useForm<FormValues>();

  const onSubmit = (data: FormValues) => {
    if (selectedMember == null) {
      setError('receiver', { type: 'manual', message: 'Receiver is required' });
      return;
    }

    setIsLoading(true);

    const paymentData = {
      senderId: owner?.id,
      receiverId: (selectedMember as Option).value.id,
      amount: data.amount,
      groupId: groupId,
    };

    console.log(paymentData.amount);
    apiClient
      .post('/payments', paymentData, {
        headers: {
          'Content-Type': 'application/json',
        },
      })
      .then(() => {
        getPayments().then((payments) => {
          setPayments(payments);
        });
        closePaymentCreationModal();
        reset();
        setSelectedMember(null);
        toast.success('Payment created successfully');
      })
      .catch((error) => {
        console.error(error);
      })
      .finally(() => {
        reset();
        setIsLoading(false);
      });
  };

  const handleMemberChange = (selectedOption: Option | null) => {
    setSelectedMember(selectedOption);
    setValue('receiver', selectedOption ? selectedOption.value : null, {
      shouldValidate: selectedOption !== null,
    });
    if (selectedOption) {
      clearErrors('receiver');
    }
  };

  return (
    <DialogRoot
      placement="center"
      size="lg"
      scrollBehavior="inside"
      open={isPaymentCreationModalOpen}
      onInteractOutside={closePaymentCreationModal}
      onEscapeKeyDown={closePaymentCreationModal}
      motionPreset="slide-in-bottom"
    >
      <DialogBackdrop
        bg="rgba(0, 0, 0, 0.4)"
        _dark={{ bg: 'rgba(0, 0, 0, 0.7 )' }}
        css={{ backdropFilter: 'blur(2px)' }}
      />
      <DialogContent
        shadow="xl"
        p={4}
        css={{
          position: 'fixed',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
        }}
      >
        <DialogCloseTrigger
          _icon={{ w: 6, h: 6 }}
          onClick={closePaymentCreationModal}
        />
        <DialogHeader p={4}>
          <DialogTitle fontSize="xl">Add money transfer</DialogTitle>
        </DialogHeader>
        <DialogBody
          data-theme={currentTheme}
          display="flex"
          alignItems="center"
          justifyContent="center"
          flexDirection="column"
          p={{ base: 2, md: 8 }}
        >
          <StyledForm onSubmit={handleSubmit(onSubmit)}>
            <Flex
              direction="column"
              justifyContent="space-evenly"
              pb={8}
              w="100%"
            >
              <FormControl isInvalid={!!errors.amount} isRequired>
                <FormLabel htmlFor="name">Amount</FormLabel>
                <Input
                  id="amount"
                  type="number"
                  color="textRaw"
                  _focus={{ outline: 'none' }}
                  {...register('amount', {
                    required: 'Amount is required',
                    min: {
                      value: 0.01,
                      message: 'Amount must be greater than 0',
                    },
                    max: {
                      value: 999999,
                      message: "You ain't that rich",
                    },
                    validate: {
                      isTwoDecimalPlaces: (value) =>
                        /^\d+(\.\d{1,2})?$/.test(value) ||
                        'Amount must have at most 2 decimal places',
                    },
                  })}
                  step="0.01"
                  min="0.01"
                />
                <FormErrorMessage
                  mt={6}
                  color="#cc0000"
                  _dark={{ color: '#ff8080' }}
                >
                  {errors.amount?.message as string}
                </FormErrorMessage>
              </FormControl>
              <FormControl mt={18} isInvalid={!!errors.receiver} isRequired>
                <FormLabel htmlFor="receiver">Receiver</FormLabel>
                <Select
                  options={memberOptions}
                  value={selectedMember}
                  onChange={handleMemberChange}
                  placeholder="Select receiver..."
                  noOptionsMessage={() => 'No members found'}
                  isMulti={false}
                  className="select"
                  maxMenuHeight={100}
                  menuShouldBlockScroll={false}
                />
                <FormErrorMessage
                  mt={6}
                  color="#cc0000"
                  _dark={{ color: '#ff8080' }}
                >
                  {errors.receiver && errors.receiver.message}
                </FormErrorMessage>
              </FormControl>
              <Button
                fontSize="lg"
                bg="primary"
                color="textBg"
                _hover={{ bg: 'hover' }}
                disabled={isLoading}
                mt={8}
                type="submit"
              >
                Submit
              </Button>
            </Flex>
          </StyledForm>
        </DialogBody>
      </DialogContent>
    </DialogRoot>
  );
};
