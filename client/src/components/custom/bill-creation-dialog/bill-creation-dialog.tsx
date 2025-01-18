import { useColorModeValue } from '@/components/ui/color-mode';
import { Bill, ExpenseForm, GroupMember, GroupMemberWithUser } from '@/types';
import {
  closeBillCreationModalAtom,
  isBillCreationModalOpenAtom,
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
import {
  Dispatch,
  FC,
  SetStateAction,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { useFieldArray, useForm, useWatch } from 'react-hook-form';
import { IoCloseOutline } from 'react-icons/io5';
import Select from 'react-select';
import { toast } from 'react-toastify';
import styled from 'styled-components';

type BillCreationDialogProps = {
  getBills: () => Promise<Bill[]>;
  setBills: Dispatch<SetStateAction<Bill[]>>;
  members: GroupMemberWithUser[];
  lender: GroupMemberWithUser | null;
  groupId: string | undefined;
};

const StyledForm = styled('form')`
  width: 100%;
`;

export const BillCreationDialog: FC<BillCreationDialogProps> = ({
  getBills,
  setBills,
  members,
  lender,
  groupId,
}) => {
  const [isBillCreationModalOpen] = useAtom(isBillCreationModalOpenAtom);
  const [, closeBillCreationModal] = useAtom(closeBillCreationModalAtom);
  const [isLoading, setIsLoading] = useState(false);
  const currentTheme = useColorModeValue('light', 'dark');
  const [selectMembers, setSelectMembers] =
    useState<GroupMemberWithUser[]>(members);

  type Option = {
    value: GroupMemberWithUser;
    label: string;
  };
  const handleCloseModal = () => {
    reset();
    setSelectMembers(members);
    closeBillCreationModal();
  };

  const memberOptions = useMemo(() => {
    return selectMembers.map((member) => {
      return { label: member.nickname, value: member };
    });
  }, [selectMembers]);

  interface FormValues {
    totalAmount: string;
    name: string;
    lender: GroupMember | null;
    expenses: ExpenseForm[];
  }

  const {
    handleSubmit,
    register,
    control,
    reset,
    setValue,
    getValues,
    setError,
    clearErrors,
    formState: { errors },
  } = useForm<FormValues>({
    defaultValues: {
      expenses: [{ borrowerId: '', amount: 0 }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'expenses',
  });

  const onSubmit = (data: FormValues) => {
    setIsLoading(true);

    const billData = {
      name: data.name,
      lenderId: lender?.id,
      totalAmount: data.totalAmount,
      groupId: groupId,
      expenses: data.expenses,
    };

    apiClient
      .post('/bills', billData, {
        headers: {
          'Content-Type': 'application/json',
        },
      })
      .then(() => {
        getBills().then((Bills) => {
          setBills(Bills);
        });
        handleCloseModal();
        toast.success('Bill created successfully');
      })
      .catch((error) => {
        console.error(error);
        toast.error('Failed to create bill, please try again later!');
      })
      .finally(() => {
        setSelectMembers(members);
        reset();
        setIsLoading(false);
      });
  };

  const handleMemberChange = (selectedOption: Option | null, index: number) => {
    const prevBorrowerOption = getValues(`expenses.${index}.borrowerId`);
    setSelectMembers((prevSelectMembers) => {
      const borrower = members.find(
        (member) => member.id === prevBorrowerOption,
      );
      const newSelectMembers = prevSelectMembers.filter(
        (member) => member.id !== selectedOption?.value.id,
      );
      if (borrower) {
        newSelectMembers.push(borrower);
        return newSelectMembers;
      }
      return newSelectMembers;
    });
    setValue(
      `expenses.${index}.borrowerId`,
      selectedOption ? selectedOption.value.id : '',
      {
        shouldValidate: selectedOption !== null,
      },
    );
  };

  const removeExpense = (selectedBorrower: string, index: number) => {
    setTimeout(() => {
      remove(index);
    }, 0);
    setSelectMembers((prevSelectMembers) => {
      const borrower = members.find((member) => member.id === selectedBorrower);
      if (borrower) {
        return [...prevSelectMembers, borrower];
      }
      return prevSelectMembers;
    });
  };
  const watchedValues = useWatch({ control });

  useEffect(() => {
    if (watchedValues.totalAmount && watchedValues.expenses) {
      const totalAmount = parseFloat(watchedValues.totalAmount);
      const sumOfAmounts = watchedValues.expenses.reduce(
        (sum, expense) => sum + (Number(expense.amount) || 0),
        0,
      );

      if (totalAmount != sumOfAmounts) {
        setError('totalAmount', {
          type: 'manual',
          message: 'Total amount must equal the sum of all expenses',
        });
      } else {
        clearErrors('totalAmount');
      }
    }
  }, [watchedValues]);

  return (
    <DialogRoot
      placement="center"
      size="lg"
      scrollBehavior="inside"
      open={isBillCreationModalOpen}
      onInteractOutside={handleCloseModal}
      onEscapeKeyDown={handleCloseModal}
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
        <DialogCloseTrigger _icon={{ w: 6, h: 6 }} onClick={handleCloseModal} />
        <DialogHeader p={4}>
          <DialogTitle fontSize="xl">Create new bill</DialogTitle>
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
              <FormControl isInvalid={!!errors.totalAmount} isRequired>
                <FormLabel htmlFor="name">Title</FormLabel>
                <Input
                  id="name"
                  type="text"
                  color="textRaw"
                  _focus={{ outline: 'none' }}
                  {...register('name', {
                    required: 'Name is required',
                    maxLength: {
                      value: 50,
                      message: 'Name must be at most 50 characters long',
                    },
                  })}
                />
                <FormErrorMessage
                  mt={6}
                  color="#cc0000"
                  _dark={{ color: '#ff8080' }}
                >
                  {errors.name?.message as string}
                </FormErrorMessage>
              </FormControl>
              <FormControl isInvalid={!!errors.totalAmount} isRequired mt={18}>
                <FormLabel htmlFor="name">Total Amount</FormLabel>
                <Input
                  id="totalAmount"
                  type="number"
                  color="textRaw"
                  _focus={{ outline: 'none' }}
                  {...register('totalAmount', {
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
                  {errors.totalAmount?.message as string}
                </FormErrorMessage>
              </FormControl>
              {fields.map((field, index) => (
                <Flex
                  mt={18}
                  gap={2}
                  direction="row"
                  justifyContent={'space-between'}
                  key={field.id}
                >
                  <FormControl
                    isInvalid={!!errors.lender}
                    isRequired
                    width={'100%'}
                  >
                    <FormLabel htmlFor="receiver">Lender</FormLabel>
                    <Select
                      options={memberOptions}
                      onChange={(e) => handleMemberChange(e, index)}
                      placeholder="Select lender..."
                      noOptionsMessage={() => 'No members found'}
                      isMulti={false}
                      className="select"
                      maxMenuHeight={100}
                      menuShouldBlockScroll={false}
                    />
                  </FormControl>
                  <FormControl isInvalid={!!errors.totalAmount} isRequired>
                    <FormLabel htmlFor="name">Amount</FormLabel>
                    <Input
                      id="amount"
                      type="number"
                      color="textRaw"
                      _focus={{ outline: 'none' }}
                      {...register(`expenses.${index}.amount`, {
                        required: 'Amount is required',
                        maxLength: {
                          value: 50,
                          message: 'Name must be at most 50 characters long',
                        },
                      })}
                    />
                  </FormControl>
                  <Button
                    bg="none"
                    color="textError"
                    minW={0}
                    w={8}
                    h={8}
                    mt={6}
                    _hover={{ bg: 'backgroundErrorHover' }}
                    onClick={() =>
                      removeExpense(fields[index].borrowerId, index)
                    }
                  >
                    <IoCloseOutline style={{ width: '25px', height: '25px' }} />
                  </Button>
                </Flex>
              ))}
              <Flex mt={8} justifyContent={'flex-end'} width={'100%'} gap={2}>
                <Button
                  fontSize="lg"
                  color="textBg"
                  _hover={{ bg: 'hover' }}
                  disabled={isLoading}
                  type="button"
                  variant="outline"
                  borderColor="textDimmed"
                  onClick={() => append({ borrowerId: '', amount: 0 })}
                >
                  + Add lender
                </Button>
                <Button
                  fontSize="lg"
                  bg="primary"
                  color="textBg"
                  _hover={{ bg: 'hover' }}
                  disabled={isLoading}
                  type="submit"
                >
                  Submit
                </Button>
              </Flex>
            </Flex>
          </StyledForm>
        </DialogBody>
      </DialogContent>
    </DialogRoot>
  );
};
